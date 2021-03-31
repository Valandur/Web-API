package io.valandur.webapi;

import io.valandur.webapi.config.Config;
import io.valandur.webapi.info.ServerInfo;
import io.valandur.webapi.item.ItemStack;
import io.valandur.webapi.player.Player;
import io.valandur.webapi.player.PlayerInventory;
import io.valandur.webapi.web.WebServer;
import io.valandur.webapi.world.World;
import jakarta.ws.rs.WebApplicationException;

import java.util.Collection;
import java.util.UUID;
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


    public abstract Collection<Player> getPlayers();

    public abstract Player getPlayer(UUID uuid) throws WebApplicationException;

    public abstract PlayerInventory getPlayerInventory(UUID uuid) throws WebApplicationException;

    public abstract void addToPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException;

    public abstract void removeFromPlayerInventory(UUID uuid, Collection<ItemStack> stacks) throws WebApplicationException;

    public abstract Collection<World> getWorlds();

    public abstract ServerInfo getInfo();


    public abstract Conf getConfig(String name);


    public abstract void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException;

    public abstract <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException;
}
