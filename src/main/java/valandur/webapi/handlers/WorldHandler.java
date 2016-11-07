package valandur.webapi.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import valandur.webapi.misc.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class WorldHandler extends AbstractHandler {
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        JsonObject json = new JsonObject();
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        Server server = Sponge.getServer();
        String[] paths = target.substring(1).split("/");
        String wName = paths[0];

        if (wName.isEmpty()) {
            JsonArray arr = new JsonArray();
            Collection<World> worlds = server.getWorlds();
            for (World world : worlds) {
                arr.add(new JsonPrimitive(world.getName()));
            }
            json.add("worlds", arr);
        } else {
            Optional<World> res = server.getWorld(wName);
            if (res.isPresent()) {
                World world = res.get();
                json.addProperty("name", world.getName());
                json.addProperty("difficulty", world.getDifficulty().getName());
                json.addProperty("uuid", world.getUniqueId().toString());
                json.addProperty("dimension", world.getDimension().toString());
                JsonArray chunks = new JsonArray();
                for (Chunk chunk : world.getLoadedChunks()) {
                    JsonObject jsonChunk = new JsonObject();
                    jsonChunk.add("position", Util.positionToJson(chunk.getPosition()));
                    jsonChunk.addProperty("entities", chunk.getEntities().size());
                    jsonChunk.addProperty("difficulty", chunk.getRegionalDifficultyFactor());
                    chunks.add(jsonChunk);
                }
                json.add("loadedChunks", chunks);
                JsonObject gameRules = new JsonObject();
                for (Map.Entry<String, String> rule : world.getGameRules().entrySet()) {
                    gameRules.addProperty(rule.getKey(), rule.getValue());
                }
                json.add("gameRules", gameRules);
                json.addProperty("time", world.getProperties().getWorldTime());
                json.add("spawn", Util.positionToJson(world.getProperties().getSpawnPosition()));
                WorldBorder border = world.getWorldBorder();
                JsonObject jsonBorder = new JsonObject();
                jsonBorder.add("center", Util.positionToJson(border.getCenter()));
                jsonBorder.addProperty("diameter", border.getDiameter());
                jsonBorder.addProperty("damageAmount", border.getDamageAmount());
                jsonBorder.addProperty("damageThreshold", border.getDamageThreshold());
                jsonBorder.addProperty("warningDistance", border.getWarningDistance());
                jsonBorder.addProperty("warningTime", border.getWarningTime());
                json.add("border", jsonBorder);
            } else {
                json.addProperty("error", "World with name " + wName + " not found");
            }
        }

        PrintWriter out = response.getWriter();
        out.print(json);
        baseRequest.setHandled(true);
    }
}
