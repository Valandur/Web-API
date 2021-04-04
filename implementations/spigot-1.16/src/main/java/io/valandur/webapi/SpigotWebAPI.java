package io.valandur.webapi;

import io.valandur.webapi.config.SpigotConfig;
import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.logger.SpigotLogger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.player.SpigotPlayerService;
import io.valandur.webapi.world.SpigotWorldService;
import io.valandur.webapi.world.WorldService;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class SpigotWebAPI extends WebAPI<SpigotConfig> {

    private final SpigotWebAPIPlugin plugin;

    public SpigotWebAPIPlugin getPlugin() {
        return plugin;
    }

    public SpigotWebAPI(SpigotWebAPIPlugin plugin) {
        super();

        this.plugin = plugin;
    }

    @Override
    protected Logger createLogger() {
        return new SpigotLogger(plugin.getLogger());
    }

    @Override
    public SpigotConfig getConfig(String name) {
        return new SpigotConfig(name + ".yml", plugin);
    }

    @Override
    protected WorldService createWorldService() {
        return new SpigotWorldService(this);
    }

    @Override
    protected PlayerService createPlayerService() {
        return new SpigotPlayerService(this);
    }

    @Override
    public ServerInfo getInfo() {
        var server = plugin.getServer();
        return new ServerInfo(
                server.getMotd(),
                server.getOnlinePlayers().size(),
                server.getMaxPlayers(),
                server.getOnlineMode(),
                plugin.getUptime(),
                server.getVersion()
        );
    }

    @Override
    public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
        // TODO: Detect if we're already on the main thread
        var future = plugin.getServer().getScheduler().callSyncMethod(plugin, () -> {
            runnable.run();
            return null;
        });
        future.get();
    }

    @Override
    public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
        // TODO: Detect if we're already on the main thread
        var future = plugin.getServer().getScheduler().callSyncMethod(plugin, supplier::get);
        return future.get();
    }
}
