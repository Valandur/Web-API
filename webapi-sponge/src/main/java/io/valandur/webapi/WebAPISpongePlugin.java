package io.valandur.webapi;

import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.jvm.Plugin;

import java.nio.file.Path;

@Plugin("webapi-sponge")
public class WebAPISpongePlugin {

    private WebAPI<SpongeConfig> webapi;
    private long serverStart;

    public long getUptime() {
        return System.currentTimeMillis() - serverStart;
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;

    public Path getConfigPath() {
        return configPath;
    }

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer container;

    public PluginContainer getContainer() {
        return this.container;
    }


    @Listener
    public void onConstructPlugin(final ConstructPluginEvent event) {
        serverStart = System.currentTimeMillis();
    }

    @Listener
    public void onServerStarting(final StartingEngineEvent<Server> event) {
        webapi = new WebAPISponge(this);
        webapi.load();
        webapi.start();
    }

    @Listener
    public void onServerStopping(final StoppingEngineEvent<Server> event) {
        webapi.stop();
    }
}
