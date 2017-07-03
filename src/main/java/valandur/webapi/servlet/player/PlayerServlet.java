package valandur.webapi.servlet.player;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.servlet.ServletData;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@WebAPIServlet(basePath = "player")
public class PlayerServlet extends WebAPIBaseServlet {

    @WebAPIEndpoint(method = "GET", path = "/", perm = "list")
    public void getPlayers(ServletData data) {
        data.addJson("ok", true, false);
        data.addJson("players", cacheService.getPlayers(), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = "GET", path = "/:player", perm = "one")
    public void getPlayer(ServletData data, CachedPlayer player) {
        Optional<String> strFields = data.getQueryParam("fields");
        Optional<String> strMethods = data.getQueryParam("methods");
        if (strFields.isPresent() || strMethods.isPresent()) {
            String[] fields = strFields.map(s -> s.split(",")).orElse(new String[]{});
            String[] methods = strMethods.map(s -> s.split(",")).orElse(new String[]{});
            Tuple extra = cacheService.getExtraData(player, fields, methods);
            data.addJson("fields", extra.getFirst(), true);
            data.addJson("methods", extra.getSecond(), true);
        }

        data.addJson("ok", true, false);
        data.addJson("player", player, true);
    }

    @WebAPIEndpoint(method = "PUT", path = "/:player", perm = "change")
    public void updatePlayer(ServletData data, CachedPlayer player) {
        data.addJson("player", player, true);
    }

    @WebAPIEndpoint(method = "POST", path = "/:player/method", perm = "method")
    public void executeMethod(ServletData data) {
        String uuid = data.getPathParam("player");
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid player UUID");
            return;
        }

        Optional<ICachedPlayer> player = cacheService.getPlayer(UUID.fromString(uuid));
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

        Optional<Object> res = cacheService.executeMethod(player.get(), mName, params.get().getFirst(), params.get().getSecond());
        if (!res.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get world");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("player", player.get(), true);
        data.addJson("result", res.get(), true);
    }
}
