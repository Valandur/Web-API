package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.misc.Permission;
import valandur.webapi.cache.CachedTileEntity;
import valandur.webapi.cache.CachedWorld;
import valandur.webapi.cache.DataCache;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.Util;

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
                Optional<Collection<CachedTileEntity>> coll = DataCache.getTileEntities();
                if (!coll.isPresent())
                    data.addJson("tileEntities", JsonNodeFactory.instance.objectNode());
                else
                    data.addJson("tileEntities", JsonConverter.toJson(coll.get()));
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

            data.setStatus(HttpServletResponse.SC_OK);
            data.addJson("world", JsonConverter.toJson(world.get()));
            data.addJson("tileEntities", JsonConverter.toJson(tes, true));
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

            data.setStatus(HttpServletResponse.SC_OK);
            data.addJson("tileEntity", JsonConverter.toJson(te.get(), true));
        }
    }

    @Override
    @Permission(perm = "tile-entity")
    protected void handlePost(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length < 4 || paths[0].isEmpty() || paths[1].isEmpty() || paths[2].isEmpty() || paths[3].isEmpty()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
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

        Optional<CachedTileEntity> te = DataCache.getTileEntity(world.get(), Integer.parseInt(paths[1]), Integer.parseInt(paths[2]), Integer.parseInt(paths[3]));
        if (!te.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        final JsonNode reqJson = (JsonNode) data.getAttribute("body");

        if (reqJson.has("method")) {
            String mName = reqJson.get("method").asText();
            Optional<Tuple<Class[], Object[]>> params = Util.parseParams(reqJson.get("params"));

            if (!params.isPresent()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            JsonNode res = DataCache.executeMethod(te.get(), mName, params.get().getFirst(), params.get().getSecond());
            data.addJson("result", res);
        } else if (reqJson.has("field")) {
            String fName = reqJson.get("field").asText();
            JsonNode res = DataCache.getField(te.get(), fName);
            data.addJson("result", res);
        } else {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
