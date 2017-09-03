package valandur.webapi.api;

import org.spongepowered.api.Sponge;
import valandur.webapi.api.block.IBlockService;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.extension.IExtensionService;
import valandur.webapi.api.hook.IWebHookService;
import valandur.webapi.api.json.IJsonService;
import valandur.webapi.api.message.IMessageService;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.server.IServerService;
import valandur.webapi.api.servlet.IServletService;

import java.util.Optional;

/**
 * The API interface to the Web-API. This contains all the services used to communicate with the Web-API.
 * When writing a servlet or a json serializer please note that these services are already provided as class
 * variables (inherited from WebAPIBaseServlet or WebAPIBaseSerializer).
 */
public abstract class WebAPIAPI {

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
     * Gets the extension service from the Web-API. Used to load extensions to the Web-API
     * @return An optional containing the extension service if it was loaded.
     */
    public static Optional<IExtensionService> getExtensionService() {
        return Sponge.getServiceManager().provide(IExtensionService.class);
    }

    /**
     * Gets the json service from the Web-API. Used to convert objects into json.
     * @return An optional containing the json service if it was loaded.
     */
    public static Optional<IJsonService> getJsonService() {
        return Sponge.getServiceManager().provide(IJsonService.class);
    }

    /**
     * Gets the message service from the Web-API. Used to send interactive messages to players.
     * @return An optional containing the message service if it was loaded.
     */
    public static Optional<IMessageService> getMessageService() {
        return Sponge.getServiceManager().provide(IMessageService.class);
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
}
