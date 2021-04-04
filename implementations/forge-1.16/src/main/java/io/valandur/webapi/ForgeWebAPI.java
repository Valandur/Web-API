package io.valandur.webapi;

import io.valandur.webapi.config.ForgeConfig;
import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.logger.ForgeLogger;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.ForgePlayerService;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.world.ForgeWorldService;
import io.valandur.webapi.world.WorldService;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class ForgeWebAPI extends WebAPI<ForgeConfig> {

    private final ForgeWebAPIPlugin plugin;

    public ForgeWebAPI(ForgeWebAPIPlugin plugin) {
        super();

        this.plugin = plugin;
    }

    @Override
    protected Logger createLogger() {
        return new ForgeLogger(plugin.getLogger());
    }

    @Override
    public ForgeConfig getConfig(String name) {
        return new ForgeConfig(name);
    }

    @Override
    protected WorldService createWorldService() {
        return new ForgeWorldService(this);
    }

    @Override
    protected PlayerService createPlayerService() {
        return new ForgePlayerService(this);
    }

    @Override
    public ServerInfo getInfo() {
        var server = ServerLifecycleHooks.getCurrentServer();

        return new ServerInfo(
                server.getMOTD(),
                server.getPlayerList().getCurrentPlayerCount(),
                server.getMaxPlayers(),
                server.isServerInOnlineMode(),
                plugin.getUptime(),
                server.getMinecraftVersion()
        );
    }

    @Override
    public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
        ServerLifecycleHooks.getCurrentServer().runAsync(runnable);
    }

    @Override
    public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
        var result = new ArrayList<T>(1);
        ServerLifecycleHooks.getCurrentServer().runAsync(() -> result.add(supplier.get())).get();
        return result.get(0);
    }
}
