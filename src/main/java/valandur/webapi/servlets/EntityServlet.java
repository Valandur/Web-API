package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;
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

public class EntityServlet extends APIServlet {
    @Override
    @Permission(perm = "entity")
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject json = new JsonObject();
        resp.setContentType("application/json; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String[] paths = Util.getPathParts(req);

        if (paths.length == 0 || paths[0].isEmpty()) {
            JsonArray arr = new JsonArray();
            Collection<World> worlds = Sponge.getServer().getWorlds();
            for (World world : worlds) {
                Collection<Entity> entities = world.getEntities();
                for (Entity entity : entities) {
                    arr.add(JsonConverter.toJson(entity));
                }
            }
            json.add("entities", arr);
        } else {
            String eName = paths[0];

            Entity entity = null;
            Collection<World> worlds = Sponge.getServer().getWorlds();
            for (World world : worlds) {
                Optional<Entity> opt = world.getEntity(UUID.fromString(eName));
                if (opt.isPresent()) {
                    entity = opt.get();
                    break;
                }
            }

            if (entity != null) {
                json.add("entity", JsonConverter.toJson(entity, true));
            } else {
                json.addProperty("error", "Entity with uuid " + eName + " not found");
            }
        }

        PrintWriter out = resp.getWriter();
        out.print(json);
    }
}
