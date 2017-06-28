package valandur.webapi.servlet.plugin;

import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.cache.plugin.CachedPluginContainer;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@WebAPIServlet(basePath = "plugin")
public class PluginServlet extends WebAPIBaseServlet {

    @WebAPIRoute(method = "GET", path = "/", perm = "list")
    public void getPlugins(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("plugins", cacheService.getPlugins(), data.getQueryParam("details").isPresent());
    }

    @WebAPIRoute(method = "GET", path = "/:plugin", perm = "one")
    public void getPlugin(ServletData data, String pluginName) {
        Optional<CachedPluginContainer> plugin = cacheService.getPlugin(pluginName);
        if (!plugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Plugin with id '" + pluginName + "' could not be found");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("plugin", plugin.get(), true);
    }
}
