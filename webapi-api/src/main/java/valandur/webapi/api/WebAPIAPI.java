package valandur.webapi.api;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import valandur.webapi.api.block.IBlockService;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.extension.IExtensionService;
import valandur.webapi.api.hook.IWebHookService;
import valandur.webapi.api.message.IMessageService;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.serialize.ISerializeService;
import valandur.webapi.api.server.IServerService;
import valandur.webapi.api.servlet.IServletService;
import valandur.webapi.api.util.Constants;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Plugin(
        id = Constants.ID,
        version = Constants.VERSION,
        name = Constants.NAME,
        url = Constants.URL,
        description = Constants.DESCRIPTION,
        authors = {
                "Valandur"
        }
)
public class WebAPIAPI {

    @Inject
    private Logger logger;

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        logger.info(Constants.NAME + " v" + Constants.VERSION + " is loading...");

        // Reusable sync executor to run code on main server thread
        syncExecutor = Sponge.getScheduler().createSyncExecutor(this);
    }

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
    public static Optional<ISerializeService> getJsonService() {
        return Sponge.getServiceManager().provide(ISerializeService.class);
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


    // Run functions on the main server thread
    private static SpongeExecutorService syncExecutor;
    public static void runOnMain(Runnable runnable) {
        if (Sponge.getServer().isMainThread()) {
            runnable.run();
        } else {
            CompletableFuture future = CompletableFuture.runAsync(runnable, syncExecutor);
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
    public static <T> Optional<T> runOnMain(Supplier<T> supplier) {
        if (Sponge.getServer().isMainThread()) {
            T obj = supplier.get();
            return obj == null ? Optional.empty() : Optional.of(obj);
        } else {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier, syncExecutor);
            try {
                T obj = future.get();
                if (obj == null)
                    return Optional.empty();
                return Optional.of(obj);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
    }
}
