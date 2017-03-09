package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import valandur.webapi.Permission;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ClassServlet extends WebAPIServlet {

    @Override
    @Permission(perm = "class")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.addJson("classes", JsonConverter.toJson(DataCache.getClasses()));
            return;
        }

        String className = paths[0];
        for (Map.Entry<Class, JsonNode> entry : DataCache.getClasses().entrySet()) {
            if (entry.getKey().getSimpleName().equalsIgnoreCase(className)) {
                data.addJson("class", entry.getValue());
                return;
            }
        }

        try {
            Class c = Class.forName(className);
            data.addJson("class", DataCache.getClass(c));
        } catch (ClassNotFoundException e) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
