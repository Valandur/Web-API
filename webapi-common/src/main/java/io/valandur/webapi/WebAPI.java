package io.valandur.webapi;

import io.valandur.webapi.config.Config;
import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.user.User;
import io.valandur.webapi.web.WebServer;
import io.valandur.webapi.world.World;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public abstract class WebAPI<Conf extends Config> {

    protected static WebAPI<?> instance;

    public static WebAPI<?> getInstance() {
        return WebAPI.instance;
    }

    protected WebServer webServer;

    public WebAPI() {
        WebAPI.instance = this;
        this.webServer = new WebServer(this);
    }

    public void load() {
        this.webServer.load();
    }
    
    public void start() {
        this.webServer.start();
    }

    public void stop() {
        this.webServer.stop();
    }

    public abstract Logger getLogger();


    public abstract Collection<User> getUsers();

    public abstract Collection<Player> getPlayers();

    public abstract Collection<World> getWorlds();

    public abstract ServerInfo getInfo();


    public abstract Conf getConfig(String name);


    public abstract void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException;

    public abstract <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException;
}
