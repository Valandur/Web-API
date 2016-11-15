package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
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

public class TileEntityServlet extends APIServlet {
    @Override
    @Permission(perm = "tile-entity")
    protected void handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject json = new JsonObject();
        resp.setContentType("application/json; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        String[] paths = Util.getPathParts(req);

        if (paths.length < 4 || paths[0].isEmpty() || paths[1].isEmpty() || paths[2].isEmpty() || paths[3].isEmpty()) {
            JsonArray arr = new JsonArray();

            if (paths.length > 0 && !paths[0].isEmpty()) {
                String wName = paths[0];
                Optional<World> world = Util.getWorldFromString(wName);
                if (world.isPresent()) {
                    Collection<TileEntity> tes = world.get().getTileEntities();
                    for (TileEntity te : tes) {
                        arr.add(JsonConverter.toJson(te));
                    }
                } else {
                    json.addProperty("error", "World with name/uuid " + wName + " not found");
                }
            } else {
                Collection<World> worlds = Sponge.getServer().getWorlds();
                for (World world : worlds) {
                    Collection<TileEntity> tes = world.getTileEntities();
                    for (TileEntity te : tes) {
                        arr.add(JsonConverter.toJson(te));
                    }
                }
            }

            json.add("tileEntities", arr);
        } else {
            String wName = paths[0];
            int x = Integer.parseInt(paths[1]);
            int y = Integer.parseInt(paths[2]);
            int z = Integer.parseInt(paths[3]);

            Optional<World> world = Util.getWorldFromString(wName);
            if (world.isPresent()) {
                Optional<TileEntity> te = world.get().getTileEntity(x, y, z);

                if (te.isPresent()) {
                    json.add("tileEntity", JsonConverter.toJson(te.get(), true));
                } else {
                    json.addProperty("error", "TileEntity at " + x + ", " + y + ", " + z + " not found");
                }
            } else {
                json.addProperty("error", "World with name/uuid " + wName + " not found");
            }
        }

        PrintWriter out = resp.getWriter();
        out.print(json);
    }
}
