package valandur.webapi.servlet;

import valandur.webapi.WebAPI;
import valandur.webapi.annotation.WebAPISpec;
import valandur.webapi.cache.DataCache;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

public class ClassServlet extends WebAPIServlet {

    @WebAPISpec(method = "GET", path = "/", perm = "class.get")
    public void getCachedClasses(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("classes", DataCache.getClasses().keySet().stream().map(Class::getName).toArray(String[]::new), false);
    }

    @WebAPISpec(method = "GET", path = "/:class", perm = "class.get")
    public void getClass(ServletData data) {
        String className = data.getPathParam("class");

        try {
            Class c = Class.forName(className);
            data.addJson("class", DataCache.getClass(c), true);
        } catch (ClassNotFoundException e) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "The class '" + className + "' could not be found");
        }
    }

    @WebAPISpec(method = "GET", path = "/:class/subclasses", perm = "class.get")
    public void getSubclasses(ServletData data) {
        String className = data.getPathParam("class");

        try {
            Class c = Class.forName(className);
            WebAPI.getInstance().getLogger().info("Discovering all subclasses of '" + c.getName() + "'...");
            Set classes = WebAPI.getInstance().getReflections().getSubTypesOf(c);
            WebAPI.getInstance().getLogger().info("Found " + classes.size() + " subclasses of '" + c.getName() + "'");

            data.addJson("ok", true, false);
            data.addJson("base", c.getName(), false);
            data.addJson("classes", classes, false);
        } catch (ClassNotFoundException e) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "The class '" + className + "' could not be found");
        }
    }
}
