package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import valandur.webapi.Permission;
import valandur.webapi.cache.CachedTileEntity;
import valandur.webapi.cache.CachedWorld;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class TileEntityServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "tile-entity")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length < 4 || paths[0].isEmpty() || paths[1].isEmpty() || paths[2].isEmpty() || paths[3].isEmpty()) {
            if (paths.length == 0 || paths[0].isEmpty()) {
                data.setStatus(HttpServletResponse.SC_OK);
                data.getJson().add("tileEntities", JsonConverter.cacheToJson(DataCache.getTileEntities()));
                return;
            }

            String uuid = paths[0];
            if (uuid.split("-").length != 5) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
            if (!world.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Optional<Collection<CachedTileEntity>> tes = DataCache.getTileEntities(world.get());

            if (!tes.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            JsonArray arr = new JsonArray();
            for (CachedTileEntity te : tes.get()) {
                arr.add(JsonConverter.cacheToJson(te));
            }
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("world", JsonConverter.cacheToJson(world.get()));
            data.getJson().add("tileEntities", arr);
        } else {
            String uuid = paths[0];
            int x = Integer.parseInt(paths[1]);
            int y = Integer.parseInt(paths[2]);
            int z = Integer.parseInt(paths[3]);

            if (uuid.split("-").length != 5) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
            if (!world.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Optional<CachedTileEntity> te = DataCache.getTileEntity(world.get(), x, y, z);

            if (!te.isPresent()) {
                data.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            if (paths.length == 4 || paths[4].isEmpty()) {
                data.setStatus(HttpServletResponse.SC_OK);
                data.getJson().add("tileEntity", JsonConverter.cacheToJson(te.get(), true));
                return;
            }

            if (paths[4].equalsIgnoreCase("raw")) {
                Optional<Object> tileEnt = te.get().getLive();
                data.setStatus(HttpServletResponse.SC_OK);
                data.getJson().add("tileEntity", JsonConverter.toRawJson(tileEnt));
            } else {
                data.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
}
