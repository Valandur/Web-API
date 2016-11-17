package valandur.webapi.servlets;

import valandur.webapi.Permission;
import valandur.webapi.cache.CachedPlayer;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class PlayerServlet extends APIServlet {
    @Override
    @Permission(perm = "player")
    protected Optional<CompletableFuture> handleGet(ServletData data) throws ServletException, IOException {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("players", JsonConverter.toJson(DataCache.getPlayers()));
        } else {
            String pName = paths[0];
            Optional<CachedPlayer> player = DataCache.getPlayer(pName);
            if (player.isPresent()) {
                data.setStatus(HttpServletResponse.SC_OK);
                data.getJson().add("player", JsonConverter.toJson(player.get(), true));
            } else {
                data.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }

        return Optional.empty();
    }
}
