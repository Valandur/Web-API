package io.valandur.webapi;

import io.valandur.webapi.config.SpigotConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotWebAPIPlugin extends JavaPlugin {

    private WebAPI<SpigotConfig> webapi;
    private long serverStart;

    public long getUptime() {
        return System.currentTimeMillis() - serverStart;
    }

    @Override
    public void onLoad() {
        serverStart = System.currentTimeMillis();

        webapi = new SpigotWebAPI(this);
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
}
