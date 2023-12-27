package io.valandur.webapi.spigot;

import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.common.WebAPIBase;
import io.valandur.webapi.config.InfoConfig;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.spigot.chat.SpigotChatService;
import io.valandur.webapi.spigot.config.SpigotInfoConfig;
import io.valandur.webapi.spigot.config.SpigotSecurityConfig;
import io.valandur.webapi.spigot.config.SpigotWebConfig;
import io.valandur.webapi.spigot.entity.SpigotEntityService;
import io.valandur.webapi.spigot.logger.SpigotLogger;
import io.valandur.webapi.spigot.player.SpigotPlayerService;
import io.valandur.webapi.spigot.info.SpigotInfoService;
import io.valandur.webapi.spigot.world.SpigotWorldService;
import io.valandur.webapi.world.WorldService;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class SpigotWebAPI extends WebAPIBase<SpigotWebAPI, SpigotWebAPIPlugin> {

  public SpigotWebAPI(SpigotWebAPIPlugin plugin) {
    super(plugin);
  }

  @Override
  public String getVersion() {
    return "";
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
  public SpigotWebConfig createWebConfig() {
    return new SpigotWebConfig(plugin);
  }
  @Override
  protected InfoConfig createInfoConfig() {
    return new SpigotInfoConfig();
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
  protected InfoService<SpigotWebAPI> createInfoService() {
    return new SpigotInfoService(this);
  }
  @Override
  protected ChatService<SpigotWebAPI> createChatService() {
    return new SpigotChatService(this);
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
