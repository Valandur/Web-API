package valandur.webapi.servlets;

import valandur.webapi.Permission;
import valandur.webapi.cache.CachedEntity;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class EntityServlet extends APIServlet {
    @Override
    @Permission(perm = "entity")
    protected Optional<CompletableFuture> handleGet(ServletData data) throws ServletException, IOException {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("entities", JsonConverter.toJson(DataCache.getEntities()));
        } else {
            String eUUID = paths[0];
            Optional<CachedEntity> player = DataCache.getEntity(eUUID);
            if (player.isPresent()) {
                data.setStatus(HttpServletResponse.SC_OK);
                data.getJson().add("entity", JsonConverter.toJson(player.get(), true));
            } else {
                data.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }

        return Optional.empty();
    }
}
