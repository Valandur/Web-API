package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.Permission;
import valandur.webapi.cache.CachedEntity;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.Util;

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
            data.addJson("entities", JsonConverter.toJson(DataCache.getEntities()));
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
            data.addJson("entity", JsonConverter.toJson(entity.get(), true));
            return;
        }

        if (paths[1].equalsIgnoreCase("raw")) {
            JsonNode res = DataCache.getJacksonLive(entity.get());
            data.setStatus(HttpServletResponse.SC_OK);
            data.addJson("entity", res);
        } else {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    @Permission(perm = "entity")
    protected void handlePost(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
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

        final JsonObject reqJson = (JsonObject) data.getAttribute("body");

        String mName = reqJson.get("method").getAsString();
        Optional<Tuple<Class[], Object[]>> params = Util.parseParams(reqJson.getAsJsonArray("params"));

        if (!params.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        JsonNode res = DataCache.executeMethod(entity.get(), mName, params.get().getFirst(), params.get().getSecond());
        data.addJson("result", res);
    }
}
