package valandur.webapi.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.mutable.entity.GameModeData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.SimpleServiceManager;
import org.spongepowered.api.world.World;
import valandur.webapi.misc.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Optional;

public class PlayerHandler extends AbstractHandler {

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        JsonObject json = new JsonObject();
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Server server = Sponge.getServer();
        String[] paths = target.substring(1).split("/");
        String pName = paths[0];

        if (pName.isEmpty()) {
            JsonArray arr = new JsonArray();
            Collection<Player> players = server.getOnlinePlayers();
            for (Player player : players) {
                arr.add(new JsonPrimitive(player.getName()));
            }
            json.addProperty("maxPlayers", server.getMaxPlayers());
            json.add("players", arr);
        } else {
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


        PrintWriter out = response.getWriter();
        out.print(json);
        baseRequest.setHandled(true);
    }
}
