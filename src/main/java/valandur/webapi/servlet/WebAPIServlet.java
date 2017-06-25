package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.annotation.WebAPISpec;
import valandur.webapi.misc.TreeNode;
import valandur.webapi.misc.Util;
import valandur.webapi.permission.Permission;
import valandur.webapi.permission.Permissions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class WebAPIServlet extends HttpServlet {

    private void handleVerb(String verb, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Methods","GET,PUT,POST,DELETE");
        resp.addHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");

        // Return early if OPTIONS
        if (req.getMethod().equals("OPTIONS") ) {
            return;
        }

        resp.setContentType("application/json; charset=utf-8");

        try {
            // Get all methods that are correctly annotated and match the verb
            List<Tuple<Method, WebAPISpec>> methods = Arrays.stream(this.getClass().getMethods())
                    .filter(m -> m.isAnnotationPresent(WebAPISpec.class))
                    .map(m -> new Tuple<>(m, m.getAnnotation(WebAPISpec.class)))
                    .filter(m -> m.getSecond().method().equalsIgnoreCase(verb))
                    .collect(Collectors.toList());

            // Find the most suitable method according to the path
            List<String> pathParts = Util.getPathParts(req.getPathInfo());
            Map<String, String> bestMatches = null;
            Tuple<Method, WebAPISpec> bestTuple = null;

            for (Tuple<Method, WebAPISpec> tuple : methods) {
                WebAPISpec spec = tuple.getSecond();
                List<String> specPathParts = Util.getPathParts(spec.path());

                if (specPathParts.size() != pathParts.size())
                    continue;

                boolean doesMatch = true;
                Map<String, String> matches = new HashMap<>();
                for (int i = 0; i < specPathParts.size(); i++) {
                    if (!specPathParts.get(i).startsWith(":")) {
                        if (!specPathParts.get(i).equalsIgnoreCase(pathParts.get(i))) {
                            doesMatch = false;
                            break;
                        }
                    } else {
                        String param = specPathParts.get(i).substring(1);
                        matches.put(param, pathParts.get(i));
                    }
                }

                if (!doesMatch)
                    continue;

                if (bestMatches == null || matches.size() < bestMatches.size()) {
                    bestMatches = matches;
                    bestTuple = tuple;
                }
            }

            if (bestTuple == null) {
                // We couldn't find a method
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown/Invalid request path");
                return;
            }

            Method method = bestTuple.getFirst();
            method.setAccessible(true);

            if (method.isAnnotationPresent(Permission.class)) {
                String[] reqPerms = method.getAnnotation(Permission.class).perm().split("\\.");
                TreeNode<String, Boolean> permissions = (TreeNode<String, Boolean>)req.getAttribute("perms");

                if (permissions == null) {
                    WebAPI.getInstance().getLogger().warn(req.getRemoteAddr() + " does not have permisson to access " + req.getRequestURI());
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized");
                    return;
                }

                TreeNode<String, Boolean> methodPerms = Permissions.subPermissions(permissions, reqPerms);
                if (!methodPerms.getValue()) {
                    WebAPI.getInstance().getLogger().warn(req.getRemoteAddr() + " does not have permission to access " + req.getRequestURI());
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Not authorized");
                    return;
                }

                req.setAttribute("dataPerms", methodPerms);
            } else {
                req.setAttribute("dataPerms", Permissions.permitAllNode());
            }

            if (verb.equalsIgnoreCase("Post") || verb.equalsIgnoreCase("Put")) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(req.getReader());
                    req.setAttribute("body", node);
                } catch (Exception e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }

            ServletData data = new ServletData(req, resp, bestMatches);

            method.invoke(this, data);
            PrintWriter out = data.getWriter();

            ObjectMapper om = new ObjectMapper();
            if (!data.isErrorSent()) {
                out.write(om.writeValueAsString(data.getNode()));
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            // Error executing the method
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleVerb("Get", req, resp);
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleVerb("Post", req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleVerb("Put", req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleVerb("Delete", req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.handleVerb("Options", req, resp);
    }

    protected void handleGet(ServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not implemented");
    }

    protected void handlePost(ServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not implemented");
    }

    protected void handlePut(ServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not implemented");
    }

    protected void handleDelete(ServletData data) {
        data.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "Not implemented");
    }
}
