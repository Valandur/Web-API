package valandur.webapi.servlet.clazz;

import valandur.webapi.WebAPI;
import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@WebAPIServlet(basePath = "class")
public class ClassServlet extends WebAPIBaseServlet {

    @WebAPIRoute(method = "GET", path = "/", perm = "list")
    public void getCachedClasses(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("classes", cacheService.getClasses().keySet().stream().map(Class::getName).toArray(String[]::new), false);
    }

    @WebAPIRoute(method = "GET", path = "/:class", perm = "one")
    public void getClass(ServletData data, String className) {
        try {
            Class c = Class.forName(className);
            data.addJson("class", cacheService.getClass(c), true);
        } catch (ClassNotFoundException e) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "The class '" + className + "' could not be found");
        }
    }

    @WebAPIRoute(method = "GET", path = "/:class/subclasses", perm = "subclasses")
    public void getSubclasses(ServletData data, String className) {
        try {
            Class c = Class.forName(className);
            WebAPI.getLogger().info("Discovering all subclasses of '" + c.getName() + "'...");
            Set classes = WebAPI.getReflections().getSubTypesOf(c);
            WebAPI.getLogger().info("Found " + classes.size() + " subclasses of '" + c.getName() + "'");

            data.addJson("ok", true, false);
            data.addJson("base", c.getName(), false);
            data.addJson("classes", classes, false);
        } catch (ClassNotFoundException e) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "The class '" + className + "' could not be found");
        }
    }
}
