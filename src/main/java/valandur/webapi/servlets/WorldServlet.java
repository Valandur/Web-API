package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBorder;
import valandur.webapi.misc.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class WorldServlet extends APIServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject json = new JsonObject();
        resp.setContentType("application/json; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String[] paths = this.getPathParts(req);

        if (paths.length == 0) {
            JsonArray arr = new JsonArray();
            Collection<World> worlds = Sponge.getServer().getWorlds();
            for (World world : worlds) {
                arr.add(new JsonPrimitive(world.getName()));
            }
            json.add("worlds", arr);
        } else {
            String wName = paths[0];
            Optional<World> res = Sponge.getServer().getWorld(wName);

            if (res.isPresent()) {
                World world = res.get();
                json.addProperty("name", world.getName());
                json.addProperty("uuid", world.getUniqueId().toString());
                json.addProperty("difficulty", world.getDifficulty().getName());
                json.addProperty("dimension", world.getDimension().toString());
                JsonArray chunks = new JsonArray();
                for (Chunk chunk : world.getLoadedChunks()) {
                    JsonObject jsonChunk = new JsonObject();
                    jsonChunk.add("position", Util.positionToJson(chunk.getPosition()));
                    jsonChunk.addProperty("entities", chunk.getEntities().size());
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

        PrintWriter out = resp.getWriter();
        out.print(json);
    }
}
