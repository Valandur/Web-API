package io.valandur.webapi;

import io.valandur.webapi.config.SpongeConfig;
import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.logger.SpongeLogger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.player.SpongePlayerService;
import io.valandur.webapi.world.SpongeWorldService;
import io.valandur.webapi.world.WorldService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

public class SpongeWebAPI extends WebAPI<SpongeConfig> {

    private final SpongeWebAPIPlugin plugin;
    private final ExecutorService syncExecutor;

    public SpongeWebAPI(SpongeWebAPIPlugin plugin) {
        super();

        this.plugin = plugin;
        this.syncExecutor = Sponge.server().scheduler().createExecutor(plugin.getContainer());
    }

    @Override
    protected Logger createLogger() {
        return new SpongeLogger(plugin.getLogger());
    }

    @Override
    public SpongeConfig getConfig(String name) {
        var confName = name + ".conf";
        return new SpongeConfig(confName, plugin.getConfigPath().resolve(confName));
    }

    @Override
    protected WorldService createWorldService() {
        return new SpongeWorldService(this);
    }

    @Override
    protected PlayerService createPlayerService() {
        return new SpongePlayerService(this);
    }

    @Override
    public ServerInfo getInfo() {
        var server = Sponge.server();

        return new ServerInfo(
                SpongeComponents.plainSerializer().serialize(server.motd()),
                server.onlinePlayers().size(),
                server.maxPlayers(),
                server.isOnlineModeEnabled(),
                plugin.getUptime(),
                Sponge.platform().minecraftVersion().name()
        );
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
