package io.valandur.webapi;

import io.valandur.webapi.config.Config;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.server.ServerService;
import io.valandur.webapi.world.WorldService;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public abstract class WebAPI<T extends WebAPI<T, Conf>, Conf extends Config> {

    protected static WebAPI<?, ?> instance;

    public static WebAPI<?, ?> getInstance() {
        return WebAPI.instance;
    }

    protected Logger logger;

    public Logger getLogger() {
        return logger;
    }

    protected WorldService<T> worldService;

    public WorldService<T> getWorldService() {
        return worldService;
    }

    protected PlayerService<T> playerService;

    public PlayerService<T> getPlayerService() {
        return playerService;
    }

    protected ServerService<T> serverService;

    public ServerService<T> getServerService() {
        return serverService;
    }


    public WebAPI() {
        WebAPI.instance = this;
    }

    public void load() {
        logger = createLogger();
        worldService = createWorldService();
        playerService = createPlayerService();
        serverService = createServerService();
    }

    public abstract Conf getConfig(String name);

    protected abstract Logger createLogger();

    protected abstract WorldService<T> createWorldService();

    protected abstract PlayerService<T> createPlayerService();

    protected abstract ServerService<T> createServerService();

    public abstract void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException;

    public abstract <S> S runOnMain(Supplier<S> supplier) throws ExecutionException, InterruptedException;
}
