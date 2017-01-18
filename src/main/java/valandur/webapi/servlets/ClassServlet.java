package valandur.webapi.servlets;

import com.google.gson.*;
import valandur.webapi.Permission;
import valandur.webapi.cache.DataCache;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ClassServlet extends WebAPIServlet {

    @Override
    @Permission(perm = "class")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            JsonArray arr = new JsonArray();
            for (Map.Entry<Class, JsonElement> entry : DataCache.getClasses().entrySet()) {
                arr.add(new JsonPrimitive(entry.getKey().getSimpleName()));
            }
            data.getJson().add("classes", arr);
            return;
        }

        String className = paths[0];
        for (Map.Entry<Class, JsonElement> entry : DataCache.getClasses().entrySet()) {
            if (entry.getKey().getSimpleName().equalsIgnoreCase(className)) {
                data.getJson().add("class", entry.getValue());
                return;
            }
        }

        try {
            Class c = Class.forName(className);
            data.getJson().add("class", DataCache.getClass(c));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
