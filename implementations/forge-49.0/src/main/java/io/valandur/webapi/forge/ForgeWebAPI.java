package io.valandur.webapi.forge;

import io.valandur.webapi.AsyncTask;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.command.CommandService;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.forge.chat.ForgeChatService;
import io.valandur.webapi.forge.command.ForgeCommandService;
import io.valandur.webapi.forge.entity.ForgeEntityService;
import io.valandur.webapi.forge.hook.ForgeHookConfig;
import io.valandur.webapi.forge.hook.ForgeHookService;
import io.valandur.webapi.forge.info.ForgeInfoConfig;
import io.valandur.webapi.forge.info.ForgeInfoService;
import io.valandur.webapi.forge.logger.ForgeLogger;
import io.valandur.webapi.forge.player.ForgePlayerService;
import io.valandur.webapi.forge.security.ForgeSecurityConfig;
import io.valandur.webapi.forge.web.ForgeWebConfig;
import io.valandur.webapi.forge.world.ForgeWorldService;
import io.valandur.webapi.hook.HookConfig;
import io.valandur.webapi.hook.HookService;
import io.valandur.webapi.info.InfoConfig;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.security.SecurityConfig;
import io.valandur.webapi.web.WebConfig;
import io.valandur.webapi.world.WorldService;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ForgeWebAPI extends WebAPI<ForgeWebAPI, ForgeWebAPIPlugin> {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public ForgeWebAPI(ForgeWebAPIPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getVersion() {
        return "";
    }

    @Override
    protected Logger createLogger() {
        return new ForgeLogger();
    }

    @Override
    protected SecurityConfig createSecurityConfig() {
        return new ForgeSecurityConfig();
    }

    @Override
    protected WebConfig createWebConfig() {
        return new ForgeWebConfig();
    }

    @Override
    protected InfoConfig createInfoConfig() {
        return new ForgeInfoConfig();
    }

    @Override
    protected HookConfig createHookConfig() {
        return new ForgeHookConfig();
    }

    @Override
    protected WorldService<ForgeWebAPI> createWorldService() {
        return new ForgeWorldService(this);
    }

    @Override
    protected PlayerService<ForgeWebAPI> createPlayerService() {
        return new ForgePlayerService(this);
    }

    @Override
    protected EntityService<ForgeWebAPI> createEntityService() {
        return new ForgeEntityService(this);
    }

    @Override
    protected InfoService<ForgeWebAPI> createInfoService() {
        return new ForgeInfoService(this);
    }

    @Override
    protected ChatService<ForgeWebAPI> createChatService() {
        return new ForgeChatService(this);
    }

    @Override
    protected CommandService<ForgeWebAPI> createCommandService() {
        return new ForgeCommandService(this);
    }

    @Override
    protected HookService<ForgeWebAPI> createHookService() {
        return new ForgeHookService(this);
    }

    @Override
    public <S> S runOnMain(Callable<S> callable) throws Exception {
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
