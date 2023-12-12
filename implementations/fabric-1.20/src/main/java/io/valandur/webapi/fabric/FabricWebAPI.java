package io.valandur.webapi.fabric;

import io.valandur.webapi.common.WebAPIBase;
import io.valandur.webapi.config.SecurityConfig;
import io.valandur.webapi.config.ServerConfig;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.fabric.config.FabricSecurityConfig;
import io.valandur.webapi.fabric.config.FabricServerConfig;
import io.valandur.webapi.fabric.logger.FabricLogger;
import io.valandur.webapi.fabric.world.FabricWorldService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.server.ServerService;
import io.valandur.webapi.world.WorldService;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class FabricWebAPI extends WebAPIBase<FabricWebAPI> {

  private final FabricWebAPIPlugin plugin;

  public FabricWebAPIPlugin getPlugin() {
    return plugin;
  }

  public FabricWebAPI(FabricWebAPIPlugin plugin) {
    super();

    this.plugin = plugin;
  }

  @Override
  protected SecurityConfig createSecurityConfig() {
    return new FabricSecurityConfig();
  }

  @Override
  protected ServerConfig createServerConfig() {
    return new FabricServerConfig();
  }

  @Override
  protected Logger createLogger() {
    return new FabricLogger();
  }

  @Override
  protected WorldService<FabricWebAPI> createWorldService() {
    return new FabricWorldService(this);
  }

  @Override
  protected PlayerService<FabricWebAPI> createPlayerService() {
    return null;
  }

  @Override
  protected EntityService<FabricWebAPI> createEntityService() {
    return null;
  }

  @Override
  protected ServerService<FabricWebAPI> createServerService() {
    return null;
  }

  @Override
  public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
    plugin.getServer().execute(runnable);
  }

  @Override
  public <S> S runOnMain(Supplier<S> supplier) throws ExecutionException, InterruptedException {
    var res = plugin.getServer().submit(supplier);
    return res.get();
  }
}
