package valandur.webapi.servlets;

import valandur.webapi.Permission;
import valandur.webapi.cache.CachedPlugin;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PluginServlet extends APIServlet {
    @Override
    @Permission(perm = "plugin")
    protected Optional<CompletableFuture> handleGet(ServletData data) throws ServletException, IOException {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("plugins", JsonConverter.toJson(DataCache.getPlugins()));
        } else {
            String pName = paths[0];
            Optional<CachedPlugin> plugin = DataCache.getPlugin(pName);
            if (plugin.isPresent()) {
                data.setStatus(HttpServletResponse.SC_OK);
                data.getJson().add("plugin", JsonConverter.toJson(plugin.get(), true));
            } else {
                data.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }

        return Optional.empty();
    }
}
