package io.valandur.webapi;

import io.valandur.webapi.config.Config;
import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.web.WebServer;
import io.valandur.webapi.world.WorldService;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public abstract class WebAPI<Conf extends Config> {

    protected static WebAPI<?> instance;

    public static WebAPI<?> getInstance() {
        return WebAPI.instance;
    }

    protected Logger logger;

    public Logger getLogger() {
        return logger;
    }

    protected WorldService worldService;

    public WorldService getWorldService() {
        return worldService;
    }

    protected PlayerService playerService;

    public PlayerService getPlayerService() {
        return playerService;
    }

    protected WebServer webServer;


    public WebAPI() {
        WebAPI.instance = this;
    }

    public void load() {
        logger = createLogger();
        worldService = createWorldService();
        playerService = createPlayerService();
        webServer = new WebServer(this);
        webServer.load();
    }

    public void start() {
        webServer.start();
    }

    public void stop() {
        webServer.stop();
    }

    protected abstract Logger createLogger();

    public abstract Conf getConfig(String name);

    protected abstract WorldService createWorldService();

    protected abstract PlayerService createPlayerService();

    public abstract ServerInfo getInfo();

    public abstract void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException;

    public abstract <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException;
}
