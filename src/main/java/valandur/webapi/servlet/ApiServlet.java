package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sentry.Sentry;
import valandur.webapi.WebAPI;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.permission.PermissionService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

public class ApiServlet extends HttpServlet {

    private ServletService servletService;
    private PermissionService permissionService;


    public ApiServlet() {
        this.servletService = WebAPI.getServletService();
        this.permissionService = WebAPI.getPermissionService();
    }

    private void handleVerb(String verb, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Methods","GET,PUT,POST,DELETE");
        resp.addHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");

        // Return early if OPTIONS
        if (req.getMethod().equals("OPTIONS") ) {
            return;
        }

        try {
            Optional<MatchedRoute> optMatch = servletService.getMethod(verb, req.getPathInfo());
            if (!optMatch.isPresent()) {
                // We couldn't find a method
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown/Invalid request path");
                return;
            }

            MatchedRoute match = optMatch.get();

            String specPerm = match.getRoute().perm();
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

                req.setAttribute("dataPerms", methodPerms);
            } else {
                req.setAttribute("dataPerms", PermissionService.permitAllNode());
            }

            if (verb.equalsIgnoreCase("Post") || verb.equalsIgnoreCase("Put")) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(req.getReader());
                    req.setAttribute("body", node);
                } catch (Exception e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON request body");
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

            match.getMethod().invoke(match.getServlet(), params.toArray());

            if (!data.isDone() && !data.isErrorSent()) {
                resp.setContentType("application/json; charset=utf-8");

                try {
                    PrintWriter out = data.getWriter();
                    ObjectMapper om = new ObjectMapper();
                    out.write(om.writeValueAsString(data.getNode()));
                } catch(IllegalStateException ignored) {
                    // We already used the output buffer in stream mode, so getting it as a writer now doesn't work
                    // Just do nothing in this case, because we can assume the output was already handled by the servlet
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            // Error executing the method
            e.printStackTrace();

            if (WebAPI.reportErrors()) {
                Sentry.clearContext();
                Sentry.getContext().addExtra("verb", verb);
                Sentry.getContext().addExtra("request", req.getRequestURI());
                Sentry.capture(e);
                Sentry.clearContext();
            }

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
