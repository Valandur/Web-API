package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.Permission;
import valandur.webapi.misc.JsonConverter;
import valandur.webapi.misc.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class PlayerServlet extends APIServlet {
    @Override
    @Permission(perm = "player")
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject json = new JsonObject();
        resp.setContentType("application/json; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        Server server = Sponge.getServer();
        String[] paths = Util.getPathParts(req);

        if (paths.length == 0 || paths[0].isEmpty()) {
            JsonArray arr = new JsonArray();
            Collection<Player> players = server.getOnlinePlayers();
            for (Player player : players) {
                arr.add(JsonConverter.toJson(player));
            }
            json.addProperty("maxPlayers", server.getMaxPlayers());
            json.add("players", arr);
        } else {
            String pName = paths[0];
            Optional<Player> res = server.getPlayer(pName);
            if (!res.isPresent()) res = server.getPlayer(UUID.fromString(pName));

            if (res.isPresent()) {
                json.add("player", JsonConverter.toJson(res.get(), true));
            } else {
                json.addProperty("error", "Player with name/uuid " + pName + " not found");
            }
        }

        PrintWriter out = resp.getWriter();
        out.print(json);
    }
}
