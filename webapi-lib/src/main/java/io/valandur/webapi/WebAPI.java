package io.valandur.webapi;

import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.chat.ChatServlet;
import io.valandur.webapi.command.CommandService;
import io.valandur.webapi.entity.EntityServlet;
import io.valandur.webapi.hook.HookConfig;
import io.valandur.webapi.info.InfoConfig;
import io.valandur.webapi.info.InfoServlet;
import io.valandur.webapi.player.PlayerServlet;
import io.valandur.webapi.security.SecurityConfig;
import io.valandur.webapi.security.SecurityService;
import io.valandur.webapi.web.BaseServlet;
import io.valandur.webapi.web.WebConfig;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.hook.HookService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.web.WebServer;
import io.valandur.webapi.world.WorldService;
import io.valandur.webapi.world.WorldServlet;

import java.util.*;
import java.util.concurrent.Callable;

public abstract class WebAPI<T extends WebAPI<T, U>, U> {
    public static final String NAME = "Web-API";

    protected static WebAPI<?, ?> instance;

    public static WebAPI<?, ?> getInstance() {
        return WebAPI.instance;
    }

    protected final U plugin;

    public U getPlugin() {
        return plugin;
    }

    protected Logger logger;

    public Logger getLogger() {
        return logger;
    }

    protected Set<BaseServlet> servlets;

    public Set<BaseServlet> getServlets() {
        return this.servlets;
    }

    protected WebServer<T> webServer;

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

    protected SecurityService<T> securityService;

    public SecurityService<T> getSecurityService() {
        return securityService;
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

    public InfoService<T> getInfoService() {
        return infoService;
    }

    protected ChatService<T> chatService;

    public ChatService<T> getChatService() {
        return chatService;
    }

    protected CommandService<T> commandService;

    public CommandService<T> getCommandService() {
        return commandService;
    }

    protected HookService<T> hookService;

    public HookService<T> getHookService() {
        return hookService;
    }

    public List<Service<T>> getServices() {
        return Arrays.asList(
                securityService,
                worldService,
                playerService,
                entityService,
                infoService,
                chatService,
                commandService,
                hookService
        );
    }


    public WebAPI(U plugin) {
        if (WebAPI.instance == null) {
            WebAPI.instance = this;
        }

        this.plugin = plugin;

        // First create logger
        logger = createLogger();

        // Then create all configs (loading & saving is done by respective services)
        securityConfig = createSecurityConfig();
        webConfig = createWebConfig();
        hookConfig = createHookConfig();
        infoConfig = createInfoConfig();

        // Then create all services
        //noinspection unchecked
        securityService = new SecurityService<>((T) this);
        worldService = createWorldService();
        playerService = createPlayerService();
        entityService = createEntityService();
        chatService = createChatService();
        commandService = createCommandService();
        hookService = createHookService();
        infoService = createInfoService();

        // Then init all services
        for (var service : getServices()) {
            service.init();
        }

        // Then create servlets
        servlets = new HashSet<>();
        servlets.add(new WorldServlet());
        servlets.add(new PlayerServlet());
        servlets.add(new EntityServlet());
        servlets.add(new InfoServlet());
        servlets.add(new ChatServlet());

        // Last init webserver (doesn't start it yet)
        //noinspection unchecked
        webServer = new WebServer<>((T) this);
        webServer.init();
    }

    public void start() {
        for (var service : getServices()) {
            service.start();
        }

        webServer.start();
    }

    public void stop() {
        webServer.stop();

        for (var service : getServices()) {
            service.stop();
        }
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

    protected abstract CommandService<T> createCommandService();

    protected abstract HookService<T> createHookService();

    public abstract <S> S runOnMain(Callable<S> callable) throws Exception;

    public abstract AsyncTask runAsync(Runnable runnable);
}
