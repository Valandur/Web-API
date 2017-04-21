package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.reflections.Reflections;
import org.spongepowered.api.event.Event;
import valandur.webapi.WebAPI;
import valandur.webapi.misc.Permission;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

public class ClassServlet extends WebAPIServlet {

    @Override
    @Permission(perm = "class")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            String baseClass = data.getQueryPart("baseClass");
            if (baseClass != null) {
                try {
                    Class<?> c = Class.forName(baseClass);

                    WebAPI.getInstance().getLogger().info("Discovering all subclasses of '" + baseClass + "'...");
                    Set classes = WebAPI.getInstance().getReflections().getSubTypesOf(c);
                    WebAPI.getInstance().getLogger().info("Found " + classes.size() + " subclasses of '" + baseClass + "'");

                    data.addJson("base", c.getName());
                    data.addJson("classes", classes);
                } catch (ClassNotFoundException e) {
                    data.sendError(HttpServletResponse.SC_NOT_FOUND, "The class '" + baseClass + "' could not be found");
                }
            } else {
                ArrayNode node = JsonNodeFactory.instance.arrayNode();
                data.addJson("classes", JsonConverter.toJson(DataCache.getClasses().keySet().stream().map(Class::getName).toArray(String[]::new)));
            }
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
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "The class '" + className + "' could not be found");
        }
    }
}
