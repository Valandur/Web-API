package io.valandur.webapi;

import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.config.HookConfig;
import io.valandur.webapi.config.InfoConfig;
import io.valandur.webapi.config.SecurityConfig;
import io.valandur.webapi.config.WebConfig;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.hook.HookService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.world.WorldService;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public abstract class WebAPI<T extends WebAPI<T>> {
  protected static WebAPI<?> instance;
  public static WebAPI<?> getInstance() {
    return WebAPI.instance;
  }

  protected Logger logger;
  public Logger getLogger() {
    return logger;
  }

  protected SecurityConfig securityConfig;
  public SecurityConfig getSecurityConfig() {
    return securityConfig;
  }

  protected WebConfig webConfig;
  public WebConfig getServerConfig() {
    return webConfig;
  }

  protected InfoConfig infoConfig;
  public InfoConfig getInfoConfig() {
    return infoConfig;
  }

  protected HookConfig hookConfig;
  public HookConfig getHookConfig() {
    return hookConfig;
  }

  protected WorldService<T> worldService;
  public WorldService<T> getWorldService() {
    return worldService;
  }

  protected PlayerService<T> playerService;
  public PlayerService<T> getPlayerService() {
    return playerService;
  }

  protected EntityService<T> entityService;
  public EntityService<T> getEntityService() {
    return entityService;
  }

  protected InfoService<T> infoService;
  public InfoService<T> getServerService() {
    return infoService;
  }

  protected ChatService<T> chatService;
  public ChatService<T> getChatService() {
    return chatService;
  }

  protected HookService<T> hookService;
  public HookService<T> getHookService() {
    return hookService;
  }


  public WebAPI() {
    WebAPI.instance = this;
  }

  public void init() {
    logger = createLogger();

    securityConfig = createSecurityConfig();
    webConfig = createWebConfig();
    infoConfig = createInfoConfig();
    hookConfig = createHookConfig();

    worldService = createWorldService();
    playerService = createPlayerService();
    entityService = createEntityService();
    infoService = createInfoService();
    chatService = createChatService();
    hookService = createHookService();

    infoService.startRecording();
  }

  public abstract String getVersion();

  protected abstract Logger createLogger();

  protected abstract SecurityConfig createSecurityConfig();
  protected abstract WebConfig createWebConfig();
  protected abstract InfoConfig createInfoConfig();
  protected abstract HookConfig createHookConfig();

  protected abstract WorldService<T> createWorldService();
  protected abstract PlayerService<T> createPlayerService();
  protected abstract EntityService<T> createEntityService();
  protected abstract InfoService<T> createInfoService();
  protected abstract ChatService<T> createChatService();
  protected abstract HookService<T> createHookService();

  public abstract void runOnMain(Runnable runnable) throws ExecutionException, InterruptedException;
  public abstract <S> S runOnMain(Supplier<S> supplier) throws ExecutionException, InterruptedException;
}
