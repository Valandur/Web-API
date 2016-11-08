package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import valandur.webapi.misc.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Optional;

public class PlayerServlet extends APIServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject json = new JsonObject();
        resp.setContentType("application/json; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        Server server = Sponge.getServer();
        String[] paths = this.getPathParts(req);

        if (paths.length == 0) {
            JsonArray arr = new JsonArray();
            Collection<Player> players = server.getOnlinePlayers();
            for (Player player : players) {
                arr.add(new JsonPrimitive(player.getName()));
            }
            json.addProperty("maxPlayers", server.getMaxPlayers());
            json.add("players", arr);
        } else {
            String pName = paths[0];
            Optional<Player> res = server.getPlayer(pName);
            if (res.isPresent()) {
                Player player = res.get();
                json.addProperty("name", player.getName());
                json.addProperty("uuid", player.getUniqueId().toString());
                json.addProperty("address", player.getConnection().getAddress().toString());
                json.add("position", Util.positionToJson(player.getLocation()));
                json.addProperty("health", player.getHealthData().health().get());
                json.addProperty("maxHealth", player.getHealthData().maxHealth().get());
                json.addProperty("food", player.getFoodData().foodLevel().get());
                json.addProperty("exhaustion", player.getFoodData().exhaustion().get());
                json.addProperty("saturation", player.getFoodData().saturation().get());
                json.addProperty("gameMode", player.gameMode().get().toString());
            } else {
                json.addProperty("error", "Player with name " + pName + " not found");
            }
        }

        PrintWriter out = resp.getWriter();
        out.print(json);
    }
}
