package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.misc.Permission;
import valandur.webapi.cache.CachedWorld;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.Util;

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
            data.addJson("worlds", JsonConverter.toJson(DataCache.getWorlds()));
            return;
        }

        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (!world.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        data.setStatus(HttpServletResponse.SC_OK);
        data.addJson("world", JsonConverter.toJson(world.get(), true));
    }

    @Override
    @Permission(perm = "world")
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

        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (!world.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        final JsonNode reqJson = (JsonNode) data.getAttribute("body");

        if (reqJson.has("method")) {
            String mName = reqJson.get("method").asText();
            Optional<Tuple<Class[], Object[]>> params = Util.parseParams(reqJson.get("params"));

            if (!params.isPresent()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            JsonNode res = DataCache.executeMethod(world.get(), mName, params.get().getFirst(), params.get().getSecond());
            data.addJson("result", res);
        } else if (reqJson.has("field")) {
            String fName = reqJson.get("field").asText();
            JsonNode res = DataCache.getField(world.get(), fName);
            data.addJson("result", res);
        } else {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
