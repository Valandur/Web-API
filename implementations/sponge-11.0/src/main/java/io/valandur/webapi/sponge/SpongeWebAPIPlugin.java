package io.valandur.webapi.sponge;

import com.google.inject.Inject;
import java.nio.file.Path;

import io.valandur.webapi.hook.HookEventType;
import io.valandur.webapi.hook.event.ServerEventData;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.event.entity.living.player.KickPlayerEvent;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent.Disconnect;
import org.spongepowered.api.event.network.ServerSideConnectionEvent.Join;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("webapi-sponge-11.0")
public class SpongeWebAPIPlugin {

  private SpongeWebAPI webapi;

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

  public Logger getLogger() {
    return this.logger;
  }

  @Inject
  private PluginContainer container;

  public PluginContainer getContainer() {
    return this.container;
  }

  @Inject
  SpongeWebAPIPlugin(final PluginContainer container, final Logger logger) {
    this.container = container;
    this.logger = logger;
  }

  @Listener
  public void onConstructPlugin(final ConstructPluginEvent event) {
    serverStart = System.currentTimeMillis();
  }

  @Listener
  public void onServerStarting(final StartingEngineEvent<Server> event) {
    webapi = new SpongeWebAPI(this);

    Sponge.eventManager().registerListeners(container, webapi.getChatService());
    Sponge.eventManager().registerListeners(container, webapi.getHookService());

    webapi.start();
  }

  @Listener
  public void onServerStarted(final StartedEngineEvent<Server> event) {
    webapi.getHookService().notifyEventHooks(new ServerEventData(HookEventType.SERVER_START));
  }

  @Listener
  public void onServerStopping(final StoppingEngineEvent<Server> event) {
    webapi.getHookService().notifyEventHooks(new ServerEventData(HookEventType.SERVER_STOP));

    webapi.stop();
  }
}
