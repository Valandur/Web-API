package valandur.webapi.api;

import org.spongepowered.api.Sponge;
import valandur.webapi.WebAPI;
import valandur.webapi.api.block.IBlockService;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.hook.IWebHookService;
import valandur.webapi.api.message.IInteractiveMessageService;
import valandur.webapi.api.security.IPermissionService;
import valandur.webapi.api.serialize.ISerializeService;
import valandur.webapi.api.server.IServerService;
import valandur.webapi.api.servlet.IServletService;

import javax.ws.rs.WebApplicationException;
import java.util.Optional;
import java.util.function.Supplier;

public class WebAPIAPI {

    /**
     * Gets the block service from the Web-API. Used to fetch and manipulate blocks.
     * @return An optional containing the block service if it was loaded.
     */
    public static Optional<IBlockService> getBlockService() {
        return Sponge.getServiceManager().provide(IBlockService.class);
    }

    /**
     * Gets the cache service from the Web-API. Used to access objects like players and worlds.
     * @return An optional containing the cache service if it was loaded.
     */
    public static Optional<ICacheService> getCacheService() {
        return Sponge.getServiceManager().provide(ICacheService.class);
    }

    /**
     * Gets the json service from the Web-API. Used to convert objects into json.
     * @return An optional containing the json service if it was loaded.
     */
    public static Optional<ISerializeService> getJsonService() {
        return Sponge.getServiceManager().provide(ISerializeService.class);
    }

    /**
     * Gets the message service from the Web-API. Used to send interactive messages to players.
     * @return An optional containing the message service if it was loaded.
     */
    public static Optional<IInteractiveMessageService> getMessageService() {
        return Sponge.getServiceManager().provide(IInteractiveMessageService.class);
    }

    /**
     * Gets the permission service from the Web-API. Used to check permissions when accessing Web-API routes.
     * @return An optional containing the permission service if it was loaded.
     */
    public static Optional<IPermissionService> getPermissionService() {
        return Sponge.getServiceManager().provide(IPermissionService.class);
    }

    /**
     * Gets the server service from the Web-API. Used to access server information such as properties and average tps.
     * @return An optional containing the server service if it was loaded.
     */
    public static Optional<IServerService> getServerService() {
        return Sponge.getServiceManager().provide(IServerService.class);
    }

    /**
     * Gets the servlet service from the Web-API. Used to load servlets which contain the endpoints of the Web-API.
     * @return An optional containing the servlet service if it was loaded.
     */
    public static Optional<IServletService> getServletService() {
        return Sponge.getServiceManager().provide(IServletService.class);
    }

    /**
     * Gets the cache web hook from the Web-API. Used to notify web hooks of events.
     * @return An optional containing the web hook service if it was loaded.
     */
    public static Optional<IWebHookService> getWebHookService() {
        return Sponge.getServiceManager().provide(IWebHookService.class);
    }


    public static void runOnMain(Runnable runnable) throws WebApplicationException {
        WebAPI.runOnMain(runnable);
    }
    public static <T> T runOnMain(Supplier<T> supplier) throws WebApplicationException {
        return WebAPI.runOnMain(supplier);
    }
}
