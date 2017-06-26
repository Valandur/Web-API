package valandur.webapi.servlet.plugin;

import valandur.webapi.api.annotation.WebAPIRoute;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.servlet.IServlet;
import valandur.webapi.cache.DataCache;
import valandur.webapi.cache.plugin.CachedPluginContainer;
import valandur.webapi.servlet.ServletData;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@WebAPIServlet(basePath = "plugin")
public class PluginServlet implements IServlet {

    @WebAPIRoute(method = "GET", path = "/", perm = "list")
    public void getPlugins(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("plugins", DataCache.getPlugins(), data.getQueryParam("details").isPresent());
    }

    @WebAPIRoute(method = "GET", path = "/:plugin", perm = "one")
    public void getPlugin(ServletData data) {
        String pName = data.getPathParam("plugin");
        Optional<CachedPluginContainer> plugin = DataCache.getPlugin(pName);
        if (!plugin.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Plugin with id '" + pName + "' could not be found");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("plugin", plugin.get(), true);
    }
}
