package io.valandur.webapi;

import org.bukkit.plugin.java.JavaPlugin;

public final class WebAPIPaperPlugin extends JavaPlugin {

    private WebAPI<PaperConfig> webapi;
    private long serverStart;

    public long getUptime() {
        return System.currentTimeMillis() - serverStart;
    }

    @Override
    public void onLoad() {
        serverStart = System.currentTimeMillis();

        webapi = new WebAPIPaper(this);
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
