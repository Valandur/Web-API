package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import valandur.webapi.Permission;
import valandur.webapi.misc.JsonConverter;
import valandur.webapi.misc.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Optional;

public class PluginServlet extends APIServlet {
    @Override
    @Permission(perm = "plugin")
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject json = new JsonObject();
        resp.setContentType("application/json; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        PluginManager pm = Sponge.getGame().getPluginManager();
        String[] paths = Util.getPathParts(req);

        if (paths.length == 0 || paths[0].isEmpty()) {
            JsonArray arr = new JsonArray();
            Collection<PluginContainer> plugins = pm.getPlugins();
            for (PluginContainer pc : plugins) {
                arr.add(JsonConverter.toJson(pc));
            }
            json.add("plugins", arr);
        } else {
            String pName = paths[0];
            Optional<PluginContainer> res = pm.getPlugin(pName);
            if (res.isPresent()) {
                json.add("plugin", JsonConverter.toJson(res.get()));
            } else {
                json.addProperty("error", "Plugin with name " + pName + " not found");
            }
        }

        PrintWriter out = resp.getWriter();
        out.print(json);
    }
}
