package io.valandur.webapi.sponge;

import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.common.WebAPIBase;
import io.valandur.webapi.config.HookConfig;
import io.valandur.webapi.config.InfoConfig;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.hook.HookService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.sponge.chat.SpongeChatService;
import io.valandur.webapi.sponge.config.SpongeInfoConfig;
import io.valandur.webapi.sponge.config.SpongeSecurityConfig;
import io.valandur.webapi.sponge.config.SpongeWebConfig;
import io.valandur.webapi.sponge.entity.SpongeEntityService;
import io.valandur.webapi.sponge.logger.SpongeLogger;
import io.valandur.webapi.sponge.player.SpongePlayerService;
import io.valandur.webapi.sponge.info.SpongeInfoService;
import io.valandur.webapi.sponge.world.SpongeWorldService;
import io.valandur.webapi.world.WorldService;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;

public class SpongeWebAPI extends WebAPIBase<SpongeWebAPI, SpongeWebAPIPlugin> {

  private final ExecutorService syncExecutor;

  public SpongeWebAPI(SpongeWebAPIPlugin plugin) {
    super(plugin);

    this.syncExecutor = Sponge.server().scheduler().executor(plugin.getContainer());
  }

  @Override
  public String getVersion() {
    return plugin.getContainer().metadata().version().toString();
  }

  @Override
  public String getFlavour() {
    return Sponge.platform().container(Platform.Component.IMPLEMENTATION).metadata().id();
  }
  @Override
  public String getFlavourVersion() {
    return Sponge.platform().container(Platform.Component.IMPLEMENTATION).metadata().version().getQualifier();
  }

  @Override
  protected Logger createLogger() {
    return new SpongeLogger(plugin.getLogger());
  }

  @Override
  public SpongeSecurityConfig createSecurityConfig() {
    return new SpongeSecurityConfig(plugin);
  }
  @Override
  public SpongeWebConfig createWebConfig() {
    return new SpongeWebConfig(plugin);
  }
  @Override
  protected InfoConfig createInfoConfig() {
    return new SpongeInfoConfig(plugin);
  }
  @Override
  protected HookConfig createHookConfig() {
    return null;
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
  protected InfoService<SpongeWebAPI> createInfoService() {
    return new SpongeInfoService(this);
  }
  @Override
  protected ChatService<SpongeWebAPI> createChatService() {
    return new SpongeChatService(this);
  }
  @Override
  protected HookService<SpongeWebAPI> createHookService() {
    return null;
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
