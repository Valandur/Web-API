package io.valandur.webapi;

import io.valandur.webapi.config.SpongeConfig;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.logger.SpongeLogger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.player.SpongePlayerService;
import io.valandur.webapi.server.ServerService;
import io.valandur.webapi.server.SpongeServerService;
import io.valandur.webapi.world.SpongeWorldService;
import io.valandur.webapi.world.WorldService;
import org.spongepowered.api.Sponge;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public class SpongeWebAPI extends WebAPIBase<SpongeWebAPI, SpongeConfig> {

    private final SpongeWebAPIPlugin plugin;

    public SpongeWebAPIPlugin getPlugin() {
        return plugin;
    }

    private final ExecutorService syncExecutor;

    public SpongeWebAPI(SpongeWebAPIPlugin plugin) {
        super();

        this.plugin = plugin;
        this.syncExecutor = Sponge.server().scheduler().createExecutor(plugin.getContainer());
    }

    @Override
    public SpongeConfig getConfig(String name) {
        var confName = name + ".conf";
        return new SpongeConfig(confName, plugin.getConfigPath().resolve(confName));
    }

    @Override
    protected Logger createLogger() {
        return new SpongeLogger(plugin.getLogger());
    }

    @Override
    protected WorldService<SpongeWebAPI> createWorldService() {
        return new SpongeWorldService(this);
    }

    @Override
    protected PlayerService<SpongeWebAPI> createPlayerService() {
        return new SpongePlayerService(this);
    }

    @Override
    protected ServerService<SpongeWebAPI> createServerService() {
        return new SpongeServerService(this);
    }

    @Override
    public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
        if (Sponge.server().onMainThread()) {
            runnable.run();
        } else {
            var future = CompletableFuture.runAsync(runnable, syncExecutor);
            future.get();
        }
    }

    @Override
    public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
        if (Sponge.server().onMainThread()) {
            return supplier.get();
        } else {
            var future = CompletableFuture.supplyAsync(supplier, syncExecutor);
            return future.get();
        }
    }
}
