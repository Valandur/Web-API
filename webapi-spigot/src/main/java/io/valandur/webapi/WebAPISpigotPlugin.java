package io.valandur.webapi;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public final class WebAPISpigotPlugin extends JavaPlugin {

    private WebAPI<SpigotConfig> webapi;
    private long serverStart;

    public long getUptime() {
        return System.currentTimeMillis() - serverStart;
    }

    @Override
    public void onLoad() {
        serverStart = System.currentTimeMillis();

        webapi = new WebAPISpigot(this);
        webapi.load();
    }

    @Override
    public void onEnable() {
        webapi.start();
    }

    @Override
    public void onDisable() {
        webapi.stop();
    }


    public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
        // TODO: Detect if we're already on the main thread
        Future<?> future = getServer().getScheduler().callSyncMethod(this, () -> {
            runnable.run();
            return null;
        });
        future.get();
    }

    public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
        // TODO: Detect if we're already on the main thread
        Future<T> future = getServer().getScheduler().callSyncMethod(this, supplier::get);
        return future.get();
    }
}
