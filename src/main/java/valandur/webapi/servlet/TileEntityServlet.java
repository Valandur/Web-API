package valandur.webapi.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.jetty.http.HttpMethod;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.api.cache.tileentity.ICachedTileEntity;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.servlet.BaseServlet;
import valandur.webapi.api.servlet.Endpoint;
import valandur.webapi.api.servlet.Servlet;
import valandur.webapi.cache.world.CachedWorld;
import valandur.webapi.servlet.base.ServletData;
import valandur.webapi.util.Util;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

@Servlet(basePath = "tile-entity")
public class TileEntityServlet extends BaseServlet {

    @Endpoint(method = HttpMethod.GET, path = "/", perm = "list")
    public void getTileEntities(ServletData data) {
        Optional<String> worldUuid = data.getQueryParam("world");
        Optional<String> typeId = data.getQueryParam("type");
        Optional<String> limitString = data.getQueryParam("limit");
        Predicate<TileEntity> filter = te -> !typeId.isPresent() || te.getType().getId().equalsIgnoreCase(typeId.get());
        int limit = Integer.parseInt(limitString.orElse("0"));

        if (worldUuid.isPresent()) {
            Optional<ICachedWorld> world = cacheService.getWorld(worldUuid.get());
            if (!world.isPresent()) {
                data.sendError(HttpServletResponse.SC_BAD_REQUEST, "World with UUID '" + worldUuid + "' could not be found");
                return;
            }

            Optional<Collection<ICachedTileEntity>> tileEntities = cacheService.getTileEntities(world.get(), filter, limit);
            if (!tileEntities.isPresent()) {
                data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get tile entities");
                return;
            }

            data.addData("ok", true, false);
            data.addData("tileEntities", tileEntities.get(), data.getQueryParam("details").isPresent());
            return;
        }

        Optional<Collection<ICachedTileEntity>> coll = cacheService.getTileEntities(filter, limit);
        if (!coll.isPresent()) {
            data.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not get tile entities");
            return;
        }

        data.addData("ok", true, false);
        data.addData("tileEntities", coll.get(), data.getQueryParam("details").isPresent());
    }

    @Endpoint(method = HttpMethod.GET, path = "/:world/:x/:y/:z", perm = "one")
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
            Tuple extra = cacheService.getExtraData(te.get(), data.responseIsXml(), fields, methods);
            data.addData("fields", extra.getFirst(), true);
            data.addData("methods", extra.getSecond(), true);
        }

        data.addData("ok", true, false);
        data.addData("tileEntity", te.get(), true);
    }

    @Endpoint(method = HttpMethod.POST, path = "/:world/:x/:y/:z/method", perm = "method")
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

        data.addData("ok", true, false);
        data.addData("tileEntity", te.get(), true);
        data.addData("result", res.get(), true);
    }
}
