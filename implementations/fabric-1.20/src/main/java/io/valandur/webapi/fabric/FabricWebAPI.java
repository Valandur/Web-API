package io.valandur.webapi.fabric;

import io.valandur.webapi.AsyncTask;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.command.CommandService;
import io.valandur.webapi.fabric.command.FabricCommandService;
import io.valandur.webapi.hook.HookConfig;
import io.valandur.webapi.info.InfoConfig;
import io.valandur.webapi.security.SecurityConfig;
import io.valandur.webapi.web.WebConfig;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.fabric.chat.FabricChatService;
import io.valandur.webapi.fabric.config.FabricHookConfig;
import io.valandur.webapi.fabric.config.FabricInfoConfig;
import io.valandur.webapi.fabric.config.FabricSecurityConfig;
import io.valandur.webapi.fabric.config.FabricWebConfig;
import io.valandur.webapi.fabric.entity.FabricEntityService;
import io.valandur.webapi.fabric.hook.FabricHookService;
import io.valandur.webapi.fabric.logger.FabricLogger;
import io.valandur.webapi.fabric.player.FabricPlayerService;
import io.valandur.webapi.fabric.info.FabricInfoService;
import io.valandur.webapi.fabric.world.FabricWorldService;
import io.valandur.webapi.hook.HookService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.world.WorldService;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FabricWebAPI extends WebAPI<FabricWebAPI, FabricWebAPIPlugin> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

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
    protected HookConfig createHookConfig() {
        return new FabricHookConfig();
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
    protected CommandService<FabricWebAPI> createCommandService() {
        return new FabricCommandService(this);
    }

    @Override
    protected HookService<FabricWebAPI> createHookService() {
        return new FabricHookService(this);
    }

    @Override
    public <S> S runOnMain(Callable<S> callable) throws Exception {
        // ThreadExecutor.submit(...) checks if we're on the correct thread for us
        var res = plugin.getServer().submit(() -> {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return res.get();
    }

    @Override
    public AsyncTask runAsync(Runnable runnable) {
        var res = scheduler.submit(runnable);
        return () -> res.cancel(true);
    }
}
