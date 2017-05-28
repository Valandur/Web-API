package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.math.NumberUtils;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.permission.Permission;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class TileEntityServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "tile-entity.get")
    protected void handleGet(ServletData data) {
        String[] parts = data.getPathParts();

        if (parts.length < 1 || parts[0].isEmpty()) {
            Optional<Collection<CachedTileEntity>> coll = DataCache.getTileEntities();
            if (!coll.isPresent()) {
                data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get tile entities");
                return;
            }

            data.addJson("tileEntities", coll.get(), false);
            return;
        }

        String uuid = parts[0];
        if (!Util.isValidUUID(uuid)) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tile entity UUID");
            return;
        }

        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (!world.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "World with UUID '" + uuid + "' could not be found");
            return;
        }

        if (parts.length < 4 || !NumberUtils.isNumber(parts[1]) || !NumberUtils.isNumber(parts[2]) || !NumberUtils.isNumber(parts[3])) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tile entity coordinates");
            return;
        }

        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = Integer.parseInt(parts[3]);

        Optional<CachedTileEntity> te = DataCache.getTileEntity(world.get(), x, y, z);

        if (!te.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Tile entity in world '" + uuid + "' at [" + x + "," + y + "," + z + "] could not be found");
            return;
        }

        String strFields = data.getQueryPart("fields");
        String strMethods = data.getQueryPart("methods");
        if (strFields != null || strMethods != null) {
            String[] fields = strFields != null ? strFields.split(",") : new String[]{};
            String[] methods = strMethods != null ? strMethods.split(",") : new String[]{};
            Tuple extra = DataCache.getExtraData(te.get(), fields, methods);
            data.addJson("fields", extra.getFirst(), true);
            data.addJson("methods", extra.getSecond(), true);
        }

        data.addJson("tileEntity", te.get(), true);
    }

    @Override
    @Permission(perm = "tile-entity.post")
    protected void handlePost(ServletData data) {
        String[] parts = data.getPathParts();

        if (parts.length < 1 || parts[0].isEmpty()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
            return;
        }

        String uuid = parts[0];
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
            return;
        }

        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (!world.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "World with UUID '" + uuid + "' could not be found");
            return;
        }

        if (parts.length < 4 || !NumberUtils.isNumber(parts[1]) || !NumberUtils.isNumber(parts[2]) || !NumberUtils.isNumber(parts[3])) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tile entity coordinates");
            return;
        }

        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        int z = Integer.parseInt(parts[3]);

        Optional<CachedTileEntity> te = DataCache.getTileEntity(world.get(), x, y, z);
        if (!te.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Tile entity in world '" + uuid + "' at [" + x + "," + y + "," + z + "] could not be found");
            return;
        }

        final JsonNode reqJson = data.getRequestBody();

        if (!reqJson.has("method")) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request must define the 'method' property");
            return;
        }

        String mName = reqJson.get("method").asText();
        Optional<Tuple<Class[], Object[]>> params = Util.parseParams(reqJson.get("params"));

        if (!params.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid parameters");
            return;
        }

        Optional<Object> res = DataCache.executeMethod(te.get(), mName, params.get().getFirst(), params.get().getSecond());
        if (!res.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get tile entity");
            return;
        }

        data.addJson("result", res.get(), true);
    }
}
