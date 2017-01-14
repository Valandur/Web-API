package valandur.webapi.servlets;

import valandur.webapi.Permission;
import valandur.webapi.cache.CachedPlayer;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;

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
            data.getJson().add("players", JsonConverter.cacheToJson(DataCache.getPlayers()));
            return;
        }

        String uuid = paths[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Optional<CachedPlayer> player = DataCache.getPlayer(UUID.fromString(uuid));
        if (player.isPresent()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("player", JsonConverter.cacheToJson(player.get(), true));
        } else {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
