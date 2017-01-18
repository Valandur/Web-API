package valandur.webapi.servlets;

import valandur.webapi.Permission;
import valandur.webapi.cache.CachedPlugin;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class PluginServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "plugin")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("plugins", JsonConverter.cacheToJson(DataCache.getPlugins()));
            return;
        }

        String pName = paths[0];
        Optional<CachedPlugin> plugin = DataCache.getPlugin(pName);
        if (!plugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (paths.length == 1 || paths[1].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("plugin", JsonConverter.cacheToJson(plugin.get(), true));
            return;
        }

        if (paths[1].equalsIgnoreCase("raw")) {
            Optional<Object> p = plugin.get().getLive();
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("plugin", JsonConverter.toRawJson(p));
        } else {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
