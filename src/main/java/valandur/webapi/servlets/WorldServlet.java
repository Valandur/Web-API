package valandur.webapi.servlets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.Permission;
import valandur.webapi.cache.CachedWorld;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;
import valandur.webapi.misc.Util;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

public class WorldServlet extends WebAPIServlet {
    @Override
    @Permission(perm = "world")
    protected void handleGet(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("worlds", JsonConverter.cacheToJson(DataCache.getWorlds()));
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

        if (paths.length == 1 || paths[1].isEmpty()) {
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("world", JsonConverter.cacheToJson(world.get(), true));
            return;
        }

        if (paths[1].equalsIgnoreCase("raw")) {
            JsonElement res = DataCache.getRawLive(world.get());
            data.setStatus(HttpServletResponse.SC_OK);
            data.getJson().add("world", res);
        } else {
            data.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    @Permission(perm = "world")
    protected void handlePost(ServletData data) {
        String[] paths = data.getPathParts();

        if (paths.length == 0 || paths[0].isEmpty()) {
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

        final JsonObject reqJson = (JsonObject) data.getAttribute("body");

        String mName = reqJson.get("method").getAsString();
        Optional<Tuple<Class[], Object[]>> params = Util.parseParams(reqJson.getAsJsonArray("params"));

        if (!params.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Optional<JsonElement> res = DataCache.executeMethod(world.get(), mName, params.get().getFirst(), params.get().getSecond());
        if (!res.isPresent()) {
            data.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        data.getJson().add("result", res.get());
    }
}
