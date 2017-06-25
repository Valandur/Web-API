package valandur.webapi.servlet;

import valandur.webapi.annotation.WebAPISpec;
import valandur.webapi.cache.DataCache;
import valandur.webapi.cache.plugin.CachedPluginContainer;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class PluginServlet extends WebAPIServlet {

    @WebAPISpec(method = "GET", path = "/", perm = "plugin.get")
    public void getPlugins(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("plugins", DataCache.getPlugins(), data.getQueryParam("details").isPresent());
    }

    @WebAPISpec(method = "GET", path = "/:plugin", perm = "plugin.get")
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
