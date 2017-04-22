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
            data.addJson("players", JsonConverter.toJson(DataCache.getPlayers()));
            return;
        }

        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid player UUID");
            return;
        }

        Optional<CachedPlayer> player = DataCache.getPlayer(UUID.fromString(uuid));
        if (!player.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Player with UUID '" + uuid + "' could not be found");
            return;
        }

        String strFields = data.getQueryPart("fields");
        String strMethods = data.getQueryPart("methods");
        if (strFields != null || strMethods != null) {
            String[] fields = strFields != null ? strFields.split(",") : new String[]{};
            String[] methods = strMethods != null ? strMethods.split(",") : new String[]{};
            Tuple extra = DataCache.getExtraData(player.get(), fields, methods);
            data.addJson("fields", extra.getFirst());
            data.addJson("methods", extra.getSecond());
        }

        data.addJson("player", JsonConverter.toJson(player.get(), true));
    }

    @Override
    @Permission(perm = "player")
    protected void handlePost(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid player UUID");
            return;
        }

        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid player UUID");
            return;
        }

        Optional<CachedPlayer> player = DataCache.getPlayer(UUID.fromString(uuid));
        if (!player.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Player with UUID '" + uuid + "' could not be found");
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

        JsonNode res = DataCache.executeMethod(player.get(), mName, params.get());
        data.addJson("result", res);
    }
}
