package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.misc.Permission;
import valandur.webapi.cache.CachedPlayer;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

public class PlayerServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "player")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.addJson("players", JsonConverter.toJson(DataCache.getPlayers()));
            return;
        }

        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Optional<CachedPlayer> player = DataCache.getPlayer(UUID.fromString(uuid));
        if (!player.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        data.setStatus(HttpServletResponse.SC_OK);
        data.addJson("player", JsonConverter.toJson(player.get(), true));
    }

    @Override
    @Permission(perm = "player")
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

        Optional<CachedPlayer> player = DataCache.getPlayer(UUID.fromString(uuid));
        if (!player.isPresent()) {
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

            JsonNode res = DataCache.executeMethod(player.get(), mName, params.get().getFirst(), params.get().getSecond());
            data.addJson("result", res);
        } else if (reqJson.has("field")) {
            String fName = reqJson.get("field").asText();
            JsonNode res = DataCache.getField(player.get(), fName);
            data.addJson("result", res);
        } else {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
