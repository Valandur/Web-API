package valandur.webapi.servlets;

import valandur.webapi.Permission;
import valandur.webapi.WebAPI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class APIServlet extends HttpServlet {
    private void handleVerb(String verb, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Method method = this.getClass().getDeclaredMethod("handle" + verb, HttpServletRequest.class, HttpServletResponse.class);
            if (method.isAnnotationPresent(Permission.class)) {
                Permission perm = method.getAnnotation(Permission.class);
                List<String> permissions = (List<String>)req.getAttribute("perms");
                if (permissions == null || (!permissions.contains("*") && !permissions.contains(perm.perm()))) {
                    WebAPI.getInstance().getLogger().warn(req.getRemoteAddr() + " does not have permisson to access " + req.getRequestURI());
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }
            method.invoke(this, req, resp);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            if (e instanceof InvocationTargetException || e instanceof IllegalAccessException) {
                e.printStackTrace();
            }
            // If we get an error calling that method, or it does not exist then return an error
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
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

    protected void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
    }

    protected void handlePost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
    }
}
