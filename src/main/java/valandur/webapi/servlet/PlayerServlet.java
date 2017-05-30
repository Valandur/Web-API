package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.permission.Permission;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

public class PlayerServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "player.get")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.addJson("players", DataCache.getPlayers(), false);
            return;
        }

        String uuid = paths[0];
        if (!Util.isValidUUID(uuid)) {
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
            data.addJson("fields", extra.getFirst(), true);
            data.addJson("methods", extra.getSecond(), true);
        }

        data.addJson("player", player.get(), true);
    }

    @Override
    @Permission(perm = "player.post")
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

        final JsonNode reqJson = data.getRequestBody();

        if (!reqJson.has("method")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request must define the 'method' property");
            return;
        }

        String mName = reqJson.get("method").asText();
        Optional<Tuple<Class[], Object[]>> params = Util.parseParams(reqJson.get("params"));

        if (!params.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
            return;
        }

        Optional<Object> res = DataCache.executeMethod(player.get(), mName, params.get().getFirst(), params.get().getSecond());
        if (!res.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not execute method");
            return;
        }

        data.addJson("result", res.get(), true);
    }
}
