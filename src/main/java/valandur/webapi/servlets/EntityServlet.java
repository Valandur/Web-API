package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.misc.Permission;
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
            data.addJson("entities", JsonConverter.toJson(DataCache.getEntities()));
            return;
        }

        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity UUID");
            return;
        }

        Optional<CachedEntity> entity = DataCache.getEntity(UUID.fromString(uuid));
        if (!entity.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity with UUID '" + uuid + "' could not be found");
            return;
        }

        String strFields = data.getQueryPart("fields");
        String strMethods = data.getQueryPart("methods");
        if (strFields != null || strMethods != null) {
            String[] fields = strFields != null ? strFields.split(",") : new String[]{};
            String[] methods = strMethods != null ? strMethods.split(",") : new String[]{};
            Tuple extra = DataCache.getExtraData(entity.get(), fields, methods);
            data.addJson("fields", extra.getFirst());
            data.addJson("methods", extra.getSecond());
        }

        data.addJson("entity", JsonConverter.toJson(entity.get(), true));
    }

    @Override
    @Permission(perm = "entity")
    protected void handlePost(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid entity UUID");
            return;
        }

        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid entity UUID");
            return;
        }

        Optional<CachedEntity> entity = DataCache.getEntity(UUID.fromString(uuid));
        if (!entity.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Entity with UUID '" + uuid + "' could not be found");
            return;
        }

        final JsonNode reqJson = (JsonNode) data.getAttribute("body");

        if (!reqJson.has("method")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request must define the 'method' property");
            return;
        }

        String mName = reqJson.get("method").asText();
        Optional<Object[]> params = Util.parseParams(reqJson.get("params"));

        if (!params.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
            return;
        }

        JsonNode res = DataCache.executeMethod(entity.get(), mName, params.get());
        data.addJson("result", res);
    }
}
