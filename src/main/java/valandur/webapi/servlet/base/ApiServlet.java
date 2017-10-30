package valandur.webapi.servlet.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import valandur.webapi.WebAPI;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.permission.PermissionService;
import valandur.webapi.serialize.SerializeService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ApiServlet extends HttpServlet {

    private ServletService servletService;
    private PermissionService permissionService;
    private SerializeService serializeService;


    public ApiServlet() {
        this.servletService = WebAPI.getServletService();
        this.permissionService = WebAPI.getPermissionService();
        this.serializeService = WebAPI.getSerializeService();
    }

    private void handleVerb(String verb, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Methods","GET,PUT,POST,DELETE,OPTIONS");
        resp.addHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept, X-WEBAPI-KEY");

        WebAPI.sentryNewRequest(req);

        // Return early if OPTIONS
        if (req.getMethod().equals("OPTIONS") ) {
            return;
        }

        Optional<MatchedRoute> optMatch = servletService.getMethod(verb, req.getPathInfo());
        if (!optMatch.isPresent()) {
            // We couldn't find a method
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown/Invalid request path");
            return;
        }

        MatchedRoute match = optMatch.get();

        String specPerm = match.getRoute().perm();
        TreeNode<String, Boolean> perms;
        if (!specPerm.isEmpty()) {
            specPerm = match.getServletSpec().basePath() + "." + specPerm;
            String[] reqPerms = specPerm.split("\\.");
            TreeNode<String, Boolean> permissions = (TreeNode<String, Boolean>) req.getAttribute("perms");

            if (permissions == null) {
                WebAPI.getLogger().warn(req.getRemoteAddr() + " does not have permisson to access " + req.getRequestURI());
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized");
                return;
            }

            TreeNode<String, Boolean> methodPerms = permissionService.subPermissions(permissions, reqPerms);
            if (!methodPerms.getValue()) {
                WebAPI.getLogger().warn(req.getRemoteAddr() + " does not have permission to access " + req.getRequestURI());
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized");
                return;
            }

            perms = methodPerms;
        } else {
            perms = IPermissionService.permitAllNode();
        }

        req.setAttribute("dataPerms", perms);

        if (verb.equalsIgnoreCase("Post") || verb.equalsIgnoreCase("Put")) {
            try {
                String type = req.getContentType();

                if (type.contains("application/x-www-form-urlencoded")) {
                    Map<String, String[]> params = req.getParameterMap();
                    ObjectNode obj = JsonNodeFactory.instance.objectNode();
                    for (Map.Entry<String, String[]> entry : params.entrySet()) {
                        ArrayNode arr = JsonNodeFactory.instance.arrayNode();
                        for (String s : entry.getValue()) {
                            arr.add(s);
                        }
                        obj.set(entry.getKey(), arr);
                    }
                    req.setAttribute("body", obj);
                } else if (type.contains("application/json")) {
                    JsonNode node = serializeService.deserialize(req.getReader(), false, perms);
                    req.setAttribute("body", node);

                    WebAPI.sentryExtra("request_body", node != null ? node.toString() : "");
                } else if (type.contains("application/xml")) {
                    JsonNode node = serializeService.deserialize(req.getReader(), true, perms);
                    req.setAttribute("body", node);

                    WebAPI.sentryExtra("request_body", node != null ? node.toString() : "");
                } else {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Unknown request body type. Use either application/json, " +
                                    "application/xml or application/x-www-form-urlencoded");
                    return;
                }
            } catch (Exception e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not parse request body: " + e.getMessage());
                return;
            }
        }

        if (match.getArgumentError() != HttpServletResponse.SC_OK) {
            resp.sendError(match.getArgumentError(), match.getArgumentErrorMessage());
            return;
        }

        ServletData data = new ServletData(req, resp, match.getMatchedParts());

        List<Object> params = match.getMatchedParams();
        params.add(0, data);

        try {
            match.getMethod().invoke(match.getServlet(), params.toArray());

            // If we didn't send an error or a response yet then automatically send it now
            if (!data.isDone() && !data.isErrorSent()) {
                resp.setContentType(data.getResponseContentType());

                try {
                    data.writeResponse();
                } catch(IllegalStateException ignored) {
                    // We already used the output buffer in stream mode, so getting it as a writer now doesn't work
                    // Just do nothing in this case, because we can assume the output was already handled by the servlet
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            // Error executing the method
            e.printStackTrace();

            if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);

            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleVerb("GET", req, resp);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleVerb("POST", req, resp);
    }

    @Override
    protected final void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleVerb("PUT", req, resp);
    }

    @Override
    protected final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleVerb("DELETE", req, resp);
    }

    @Override
    protected final void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleVerb("OPTIONS", req, resp);
    }
}
