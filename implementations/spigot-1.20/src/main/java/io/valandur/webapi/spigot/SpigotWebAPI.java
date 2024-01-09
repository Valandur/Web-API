package io.valandur.webapi.spigot;

import io.valandur.webapi.AsyncTask;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.command.CommandService;
import io.valandur.webapi.hook.HookConfig;
import io.valandur.webapi.info.InfoConfig;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.hook.HookService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.spigot.chat.SpigotChatService;
import io.valandur.webapi.spigot.command.SpigotCommandService;
import io.valandur.webapi.spigot.config.SpigotInfoConfig;
import io.valandur.webapi.spigot.config.SpigotSecurityConfig;
import io.valandur.webapi.spigot.config.SpigotWebConfig;
import io.valandur.webapi.spigot.entity.SpigotEntityService;
import io.valandur.webapi.spigot.hook.SpigotHookService;
import io.valandur.webapi.spigot.logger.SpigotLogger;
import io.valandur.webapi.spigot.player.SpigotPlayerService;
import io.valandur.webapi.spigot.info.SpigotInfoService;
import io.valandur.webapi.spigot.world.SpigotWorldService;
import io.valandur.webapi.world.WorldService;

import java.util.concurrent.Callable;

public class SpigotWebAPI extends WebAPI<SpigotWebAPI, SpigotWebAPIPlugin> {

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
    protected HookConfig createHookConfig() {
        return null;
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
    protected CommandService<SpigotWebAPI> createCommandService() {
        return new SpigotCommandService(this);
    }

    @Override
    protected HookService<SpigotWebAPI> createHookService() {
        return new SpigotHookService(this);
    }

    @Override
    public <S> S runOnMain(Callable<S> callable) throws Exception {
        if (plugin.getServer().isPrimaryThread()) {
            return callable.call();
        } else {
            var res = plugin.getServer().getScheduler().callSyncMethod(plugin, callable);
            return res.get();
        }
    }

    @Override
    public AsyncTask runAsync(Runnable runnable) {
        var res = plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
        return res::cancel;
    }
}
