package valandur.webapi.servlet.tileentity;

import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.api.annotation.WebAPIEndpoint;
import valandur.webapi.api.annotation.WebAPIServlet;
import valandur.webapi.api.cache.tileentity.ICachedTileEntity;
import valandur.webapi.api.servlet.WebAPIBaseServlet;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.servlet.ServletData;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;

@WebAPIServlet(basePath = "tile-entity")
public class TileEntityServlet extends WebAPIBaseServlet {

    @WebAPIEndpoint(method = "GET", path = "/", perm = "list")
    public void getTileEntities(ServletData data) {
        Optional<Collection<ICachedTileEntity>> coll = cacheService.getTileEntities();
        if (!coll.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get tile entities");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("tileEntities", coll.get(), data.getQueryParam("details").isPresent());
    }

    @WebAPIEndpoint(method = "GET", path = "/:world/:x/:y/:z", perm = "one")
    public void getTileEntity(ServletData data, CachedWorld world, int x, int y, int z) {
        Optional<ICachedTileEntity> te = cacheService.getTileEntity(world, x, y, z);

        if (!te.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Tile entity in world '" + world.getName() + "' at [" + x + "," + y + "," + z + "] could not be found");
            return;
        }

        Optional<String> strFields = data.getQueryParam("fields");
        Optional<String> strMethods = data.getQueryParam("methods");
        if (strFields.isPresent() || strMethods.isPresent()) {
            String[] fields = strFields.map(s -> s.split(",")).orElse(new String[]{});
            String[] methods = strMethods.map(s -> s.split(",")).orElse(new String[]{});
            Tuple extra = cacheService.getExtraData(te.get(), fields, methods);
            data.addJson("fields", extra.getFirst(), true);
            data.addJson("methods", extra.getSecond(), true);
        }

        data.addJson("ok", true, false);
        data.addJson("tileEntity", te.get(), true);
    }

    @WebAPIEndpoint(method = "POST", path = "/:world/:x/:y/:z/method", perm = "method")
    public void executeMethod(ServletData data, CachedWorld world, int x, int y, int z) {
        Optional<ICachedTileEntity> te = cacheService.getTileEntity(world, x, y, z);
        if (!te.isPresent()) {
            data.sendError(HttpServletResponse.SC_NOT_FOUND, "Tile entity in world '" + world.getName() + "' at [" + x + "," + y + "," + z + "] could not be found");
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

        Optional<Object> res = cacheService.executeMethod(te.get(), mName, params.get().getFirst(), params.get().getSecond());
        if (!res.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get tile entity");
            return;
        }

        data.addJson("ok", true, false);
        data.addJson("tileEntity", te.get(), true);
        data.addJson("result", res.get(), true);
    }
}
