package io.valandur.webapi.fabric;

import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.common.WebAPIBase;
import io.valandur.webapi.config.InfoConfig;
import io.valandur.webapi.config.SecurityConfig;
import io.valandur.webapi.config.WebConfig;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.fabric.chat.FabricChatService;
import io.valandur.webapi.fabric.config.FabricInfoConfig;
import io.valandur.webapi.fabric.config.FabricSecurityConfig;
import io.valandur.webapi.fabric.config.FabricWebConfig;
import io.valandur.webapi.fabric.entity.FabricEntityService;
import io.valandur.webapi.fabric.logger.FabricLogger;
import io.valandur.webapi.fabric.player.FabricPlayerService;
import io.valandur.webapi.fabric.info.FabricInfoService;
import io.valandur.webapi.fabric.world.FabricWorldService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.world.WorldService;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;

public class FabricWebAPI extends WebAPIBase<FabricWebAPI, FabricWebAPIPlugin> {

  public FabricWebAPI(FabricWebAPIPlugin plugin) {
    super(plugin);
  }

  @Override
  public String getVersion() {
    return "";
  }

  @Override
  protected Logger createLogger() {
    return new FabricLogger();
  }

  @Override
  protected SecurityConfig createSecurityConfig() {
    return new FabricSecurityConfig();
  }
  @Override
  protected WebConfig createWebConfig() {
    return new FabricWebConfig();
  }

  @Override
  protected InfoConfig createInfoConfig() {
    return new FabricInfoConfig();
  }

  @Override
  protected WorldService<FabricWebAPI> createWorldService() {
    return new FabricWorldService(this);
  }
  @Override
  protected PlayerService<FabricWebAPI> createPlayerService() {
    return new FabricPlayerService(this);
  }
  @Override
  protected EntityService<FabricWebAPI> createEntityService() {
    return new FabricEntityService(this);
  }
  @Override
  protected InfoService<FabricWebAPI> createInfoService() {
    return new FabricInfoService(this);
  }
  @Override
  protected ChatService<FabricWebAPI> createChatService() {
    return new FabricChatService(this);
  }

  @Override
  public void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException {
    plugin.getServer().executeSync(runnable);
  }
  @Override
  public <S> S runOnMain(Supplier<S> supplier) throws ExecutionException, InterruptedException {
    var res = plugin.getServer().submit(supplier);
    return res.get();
  }

  public UUID getWorldUUID(ServerWorld world) {
    long total = 0;

    String name = "";
    var props = world.getLevelProperties();
    if (props instanceof LevelProperties) {
      name = ((LevelProperties) props).getLevelName();
    } else if (props instanceof UnmodifiableLevelProperties) {
      name = ((UnmodifiableLevelProperties) props).getLevelName();
    }
    for (var b : name.getBytes()) {
      total += b;
    }

    var dimKey = world.getDimensionKey().getValue().toString();
    for (var b : dimKey.getBytes()) {
      total += (long) b << 16;
    }

    var seed = world.getSeed();
    return new UUID(seed, total);
  }
}
