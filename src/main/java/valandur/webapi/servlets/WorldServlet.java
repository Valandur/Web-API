package valandur.webapi.servlets;

import valandur.webapi.Permission;
import valandur.webapi.cache.CachedWorld;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class WorldServlet extends APIServlet {
    @Override
    @Permission(perm = "world")
    protected Optional<CompletableFuture> handleGet(ServletData data) throws ServletException, IOException {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("worlds", JsonConverter.toJson(DataCache.getWorlds()));
        } else {
            String wName = paths[0];
            Optional<CachedWorld> world = DataCache.getWorld(wName);
            if (world.isPresent()) {
                data.setStatus(HttpServletResponse.SC_OK);
                data.getJson().add("world", JsonConverter.toJson(world.get(), true));
            } else {
                data.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }

        return Optional.empty();
    }
}
