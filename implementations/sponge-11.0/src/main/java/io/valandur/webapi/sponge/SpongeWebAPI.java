package io.valandur.webapi.sponge;

import io.valandur.webapi.common.WebAPIBase;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.server.ServerService;
import io.valandur.webapi.sponge.config.SpongeSecurityConfig;
import io.valandur.webapi.sponge.config.SpongeServerConfig;
import io.valandur.webapi.sponge.entity.SpongeEntityService;
import io.valandur.webapi.sponge.logger.SpongeLogger;
import io.valandur.webapi.sponge.player.SpongePlayerService;
import io.valandur.webapi.sponge.server.SpongeServerService;
import io.valandur.webapi.sponge.world.SpongeWorldService;
import io.valandur.webapi.world.WorldService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import org.spongepowered.api.Sponge;

public class SpongeWebAPI extends WebAPIBase<SpongeWebAPI> {

  private final SpongeWebAPIPlugin plugin;

  public SpongeWebAPIPlugin getPlugin() {
    return plugin;
  }

  private final ExecutorService syncExecutor;

  public SpongeWebAPI(SpongeWebAPIPlugin plugin) {
    super();

    this.plugin = plugin;
    this.syncExecutor = Sponge.server().scheduler().executor(plugin.getContainer());
  }

  @Override
  public SpongeSecurityConfig createSecurityConfig() {
    return new SpongeSecurityConfig(plugin);
  }

  @Override
  public SpongeServerConfig createServerConfig() {
    return new SpongeServerConfig(plugin);
  }

  @Override
  protected Logger createLogger() {
    return new SpongeLogger(plugin.getLogger());
  }

  @Override
  protected WorldService<SpongeWebAPI> createWorldService() {
    return new SpongeWorldService(this);
  }

  @Override
  protected PlayerService<SpongeWebAPI> createPlayerService() {
    return new SpongePlayerService(this);
  }

  @Override
  protected EntityService<SpongeWebAPI> createEntityService() {
    return new SpongeEntityService(this);
  }

  @Override
  protected ServerService<SpongeWebAPI> createServerService() {
    return new SpongeServerService(this);
  }

  @Override
  public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
    if (Sponge.server().onMainThread()) {
      runnable.run();
    } else {
      var future = CompletableFuture.runAsync(runnable, syncExecutor);
      future.get();
    }
  }

  @Override
  public <T> T runOnMain(Supplier<T> supplier) throws ExecutionException, InterruptedException {
    if (Sponge.server().onMainThread()) {
      return supplier.get();
    } else {
      var future = CompletableFuture.supplyAsync(supplier, syncExecutor);
      return future.get();
    }
  }
}
