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
    @Permission(perm = "class.get")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            ArrayNode node = JsonNodeFactory.instance.arrayNode();
            data.addJson("classes", JsonConverter.toJson(DataCache.getClasses().keySet().stream().map(Class::getName).toArray(String[]::new)));
            return;
        }

        String className = paths[0];

        try {
            Class c = Class.forName(className);

            if (paths.length <= 1) {
                data.addJson("class", DataCache.getClass(c));
                return;
            }

            String op = paths[1];
            if (op.equalsIgnoreCase("subclasses")) {
                WebAPI.getInstance().getLogger().info("Discovering all subclasses of '" + c.getName() + "'...");
                Set classes = WebAPI.getInstance().getReflections().getSubTypesOf(c);
                WebAPI.getInstance().getLogger().info("Found " + classes.size() + " subclasses of '" + c.getName() + "'");

                data.addJson("base", c.getName());
                data.addJson("classes", classes);
            } else {
                data.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown operation '" + op + "'");
            }
        } catch (ClassNotFoundException e) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "The class '" + className + "' could not be found");
        }
    }
}
