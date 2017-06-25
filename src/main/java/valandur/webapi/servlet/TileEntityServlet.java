package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.annotation.WebAPISpec;
import valandur.webapi.cache.DataCache;
import valandur.webapi.cache.tileentity.CachedTileEntity;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.misc.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class TileEntityServlet extends WebAPIServlet {

    @WebAPISpec(method = "GET", path = "/", perm = "tile-entity.get")
    public void getTileEntities(ServletData data) {
        Optional<Collection<CachedTileEntity>> coll = DataCache.getTileEntities();
        if (!coll.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get tile entities");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("tileEntities", coll.get(), data.getQueryParam("details").isPresent());
    }

    @WebAPISpec(method = "GET", path = "/:world/:x/:y/:z", perm = "tile-entity.get")
    public void getTileEntity(ServletData data) {
        String uuid = data.getPathParam("world");
        if (!Util.isValidUUID(uuid)) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tile entity UUID");
            return;
        }

        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (!world.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "World with UUID '" + uuid + "' could not be found");
            return;
        }

        int x = Integer.parseInt(data.getPathParam("x"));
        int y = Integer.parseInt(data.getPathParam("y"));
        int z = Integer.parseInt(data.getPathParam("z"));

        Optional<CachedTileEntity> te = DataCache.getTileEntity(world.get(), x, y, z);

        if (!te.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Tile entity in world '" + uuid + "' at [" + x + "," + y + "," + z + "] could not be found");
            return;
        }

        Optional<String> strFields = data.getQueryParam("fields");
        Optional<String> strMethods = data.getQueryParam("methods");
        if (strFields.isPresent() || strMethods.isPresent()) {
            String[] fields = strFields.map(s -> s.split(",")).orElse(new String[]{});
            String[] methods = strMethods.map(s -> s.split(",")).orElse(new String[]{});
            Tuple extra = DataCache.getExtraData(te.get(), fields, methods);
            data.addJson("fields", extra.getFirst(), true);
            data.addJson("methods", extra.getSecond(), true);
        }

        data.addJson("ok", true, false);
        data.addJson("tileEntity", te.get(), true);
    }

    @WebAPISpec(method = "POST", path = "/:world/:x/:y/:z/method", perm = "tile-entity.post")
    public void executeMethod(ServletData data) {
        String uuid = data.getPathParam("world");
        if (uuid.split("-").length != 5) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid world UUID");
            return;
        }

        Optional<CachedWorld> world = DataCache.getWorld(UUID.fromString(uuid));
        if (!world.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "World with UUID '" + uuid + "' could not be found");
            return;
        }

        int x = Integer.parseInt(data.getPathParam("x"));
        int y = Integer.parseInt(data.getPathParam("y"));
        int z = Integer.parseInt(data.getPathParam("z"));

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

        data.addJson("ok", true, false);
        data.addJson("tileEntity", te.get(), true);
        data.addJson("result", res.get(), true);
    }
}
