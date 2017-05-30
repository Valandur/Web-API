package valandur.webapi.servlet;

import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.permission.Permission;
import valandur.webapi.cache.DataCache;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class PluginServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "plugin.get")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.addJson("plugins", DataCache.getPlugins(), false);
            return;
        }

        String pName = paths[0];
        Optional<CachedPluginContainer> plugin = DataCache.getPlugin(pName);
        if (!plugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Plugin with id '" + pName + "' could not be found");
            return;
        }

        data.addJson("plugin", plugin.get(), true);
    }
}
