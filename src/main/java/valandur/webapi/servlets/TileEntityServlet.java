package valandur.webapi.servlets;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.JsonArray;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.world.World;
import valandur.webapi.Permission;
import valandur.webapi.cache.CachedTileEntity;
import valandur.webapi.cache.CachedWorld;
import valandur.webapi.cache.DataCache;
import valandur.webapi.misc.JsonConverter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TileEntityServlet extends APIServlet {
    @Override
    @Permission(perm = "tile-entity")
    protected Optional<CompletableFuture> handleGet(ServletData data) throws ServletException, IOException {
        String[] paths = data.getPathParts();

        if (paths.length < 4 || paths[0].isEmpty() || paths[1].isEmpty() || paths[2].isEmpty() || paths[3].isEmpty()) {
            if (paths.length > 0 && !paths[0].isEmpty()) {
                String wName = paths[0];
                Optional<CachedWorld> world = DataCache.getWorld(wName);
                if (world.isPresent()) {
                    Collection<CachedTileEntity> tes = DataCache.getTileEntities(world.get());
                    JsonArray arr = new JsonArray();
                    for (CachedTileEntity te : tes) {
                        arr.add(JsonConverter.toJson(te));
                    }
                    data.setStatus(HttpServletResponse.SC_OK);
                    data.getJson().add("tileEntities", arr);
                } else {
                    data.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                data.setStatus(HttpServletResponse.SC_OK);
                data.getJson().add("tileEntities", JsonConverter.toJson(DataCache.getTileEntities()));
            }
        } else {
            String wName = paths[0];
            double x = Double.parseDouble(paths[1]);
            double y = Double.parseDouble(paths[2]);
            double z = Double.parseDouble(paths[3]);

            Optional<CachedWorld> world = DataCache.getWorld(wName);
            if (world.isPresent()) {
                Optional<CachedTileEntity> te = DataCache.getTileEntity(world.get(), x, y, z);

                if (te.isPresent()) {
                    data.setStatus(HttpServletResponse.SC_OK);
                    data.getJson().add("tileEntity", JsonConverter.toJson(te.get(), true));
                } else {
                    data.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } else {
                data.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }

        return Optional.empty();
    }
}
