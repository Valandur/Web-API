package io.valandur.webapi.spigot;

import io.valandur.webapi.common.WebAPIBase;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.server.ServerService;
import io.valandur.webapi.spigot.config.SpigotSecurityConfig;
import io.valandur.webapi.spigot.config.SpigotServerConfig;
import io.valandur.webapi.spigot.entity.SpigotEntityService;
import io.valandur.webapi.spigot.logger.SpigotLogger;
import io.valandur.webapi.spigot.player.SpigotPlayerService;
import io.valandur.webapi.spigot.server.SpigotServerService;
import io.valandur.webapi.spigot.world.SpigotWorldService;
import io.valandur.webapi.world.WorldService;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class SpigotWebAPI extends WebAPIBase<SpigotWebAPI> {

  private final SpigotWebAPIPlugin plugin;

  public SpigotWebAPIPlugin getPlugin() {
    return plugin;
  }

  public SpigotWebAPI(SpigotWebAPIPlugin plugin) {
    super();

    this.plugin = plugin;
  }

  @Override
  protected Logger createLogger() {
    return new SpigotLogger(plugin.getLogger());
  }

  @Override
  public SpigotSecurityConfig createSecurityConfig() {
    return new SpigotSecurityConfig(plugin);
  }

  @Override
  public SpigotServerConfig createServerConfig() {
    return new SpigotServerConfig(plugin);
  }

  @Override
  protected WorldService<SpigotWebAPI> createWorldService() {
    return new SpigotWorldService(this);
  }

  @Override
  protected PlayerService<SpigotWebAPI> createPlayerService() {
    return new SpigotPlayerService(this);
  }

  @Override
  protected EntityService<SpigotWebAPI> createEntityService() {
    return new SpigotEntityService(this);
  }

  @Override
  protected ServerService<SpigotWebAPI> createServerService() {
    return new SpigotServerService(this);
  }


  @Override
  public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
    // TODO: Detect if we're already on the main thread
    var future = plugin.getServer().getScheduler().callSyncMethod(plugin, () -> {
      runnable.run();
      return null;
    });
    future.get();
  }

  @Override
  public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
    // TODO: Detect if we're already on the main thread
    var future = plugin.getServer().getScheduler().callSyncMethod(plugin, supplier::get);
    return future.get();
  }
}
