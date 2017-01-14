package valandur.webapi.servlets;

import valandur.webapi.Permission;
import valandur.webapi.cache.CachedWorld;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

public class WorldServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "world")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("worlds", JsonConverter.cacheToJson(DataCache.getWorlds()));
            return;
        }

        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (world.isPresent()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("world", JsonConverter.cacheToJson(world.get(), true));
        } else {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
