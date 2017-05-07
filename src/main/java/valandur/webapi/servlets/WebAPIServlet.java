package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.Permission;
import valandur.webapi.WebAPI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class WebAPIServlet extends HttpServlet {

    private void handleVerb(String verb, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");

        try {
            Method method = this.getClass().getDeclaredMethod("handle" + verb, ServletData.class);
            if (method.isAnnotationPresent(Permission.class)) {
                String[] reqPerms = method.getAnnotation(Permission.class).perm().split(".");
                List<String[]> permissions = (List<String[]>)req.getAttribute("perms");

                if (permissions == null) {
                    WebAPI.getInstance().getLogger().warn(req.getRemoteAddr() + " does not have permisson to access " + req.getRequestURI());
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                List<String[]> permsLeft = new ArrayList<>(permissions);
                for (int index = 0; index < reqPerms.length; index++) {
                    final int i = index;

                    // Check to see if we have a * permission for this level, then we can exit early
                    Optional<String[]> allPerms = permsLeft.stream()
                            .filter(ps -> ps.length > i && ps[i].equalsIgnoreCase("*"))
                            .findAny();
                    if (allPerms.isPresent()) {
                        break;
                    }

                    // Otherwise make to filter out all the permissions that don't apply
                    permsLeft = permsLeft.stream()
                            .filter(ps -> ps.length > i && ps[i].equalsIgnoreCase(reqPerms[i]))
                            .collect(Collectors.toList());

                    // If we have no more permissions left then we don't have access
                    if (permsLeft.size() == 0) {
                        WebAPI.getInstance().getLogger().warn(req.getRemoteAddr() + " does not have permission to access " + req.getRequestURI());
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                }
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

            ServletData data = new ServletData(req, resp);
            method.invoke(this, data);
            PrintWriter out = data.getWriter();

            ObjectMapper om = new ObjectMapper();
            if (!data.isErrorSent()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                out.write(om.writeValueAsString(data.getNode()));
            }
        } catch (NoSuchMethodException e) {
            // Method does not exist (endpoint/verb not supported)
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (InvocationTargetException | IllegalAccessException e) {
            // Error executing the method
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
