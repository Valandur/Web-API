package io.valandur.webapi;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.SpongeExecutorService;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Plugin(
        id = "webapi-sponge",
        name = "WebAPI",
        description = "http(s) API for minecraft",
        url = "https://github.com/Valandur/web-api",
        authors = {
                "Valandur"
        }
)
public class WebAPISpongePlugin {

    private WebAPI<SpongeConfig> webapi;
    private long serverStart;
    private SpongeExecutorService syncExecutor;

    public long getUptime() {
        return System.currentTimeMillis() - serverStart;
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;

    public Path getConfigPath() {
        return configPath;
    }

    @Inject
    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    @Listener
    public void onInitialize(GameInitializationEvent event) {
        syncExecutor = Sponge.getScheduler().createSyncExecutor(this);

        serverStart = System.currentTimeMillis();

        webapi = new WebAPISponge(this);
        webapi.load();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        webapi.start();
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        webapi.stop();
    }


    public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
        if (Sponge.getServer().isMainThread()) {
            runnable.run();
        } else {
            CompletableFuture<Void> future = CompletableFuture.runAsync(runnable, syncExecutor);
            future.get();
        }
    }

    public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
        if (Sponge.getServer().isMainThread()) {
            return supplier.get();
        } else {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier, syncExecutor);
            return future.get();
        }
    }
}
