package valandur.webapi.servlets;

import valandur.webapi.Permission;
import valandur.webapi.cache.CachedEntity;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

public class EntityServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "entity")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("entities", JsonConverter.cacheToJson(DataCache.getEntities()));
            return;
        }

        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Optional<CachedEntity> entity = DataCache.getEntity(UUID.fromString(uuid));
        if (!entity.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (paths.length == 1 || paths[1].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("entity", JsonConverter.cacheToJson(entity.get(), true));
            return;
        }

        if (paths[1].equalsIgnoreCase("raw")) {
            Optional<Object> e = entity.get().getLive();
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("entity", JsonConverter.toRawJson(e));
        } else {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
