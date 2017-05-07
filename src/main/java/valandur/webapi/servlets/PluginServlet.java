package valandur.webapi.servlets;

import valandur.webapi.misc.Permission;
import valandur.webapi.cache.CachedPlugin;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class PluginServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "plugin.get")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.addJson("plugins", JsonConverter.toJson(DataCache.getPlugins()));
            return;
        }

        String pName = paths[0];
        Optional<CachedPlugin> plugin = DataCache.getPlugin(pName);
        if (!plugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Plugin with id '" + pName + "' could not be found");
            return;
        }

        data.addJson("plugin", JsonConverter.toJson(plugin.get(), true));
    }
}
