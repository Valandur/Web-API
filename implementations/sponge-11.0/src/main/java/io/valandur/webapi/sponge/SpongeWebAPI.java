package io.valandur.webapi.sponge;

import io.valandur.webapi.AsyncTask;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.command.CommandService;
import io.valandur.webapi.hook.HookConfig;
import io.valandur.webapi.info.InfoConfig;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.hook.HookService;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.sponge.chat.SpongeChatService;
import io.valandur.webapi.sponge.command.SpongeCommandService;
import io.valandur.webapi.sponge.hook.SpongeHookConfig;
import io.valandur.webapi.sponge.info.SpongeInfoConfig;
import io.valandur.webapi.sponge.security.SpongeSecurityConfig;
import io.valandur.webapi.sponge.web.SpongeWebConfig;
import io.valandur.webapi.sponge.entity.SpongeEntityService;
import io.valandur.webapi.sponge.hook.SpongeHookService;
import io.valandur.webapi.sponge.info.SpongeInfoService;
import io.valandur.webapi.sponge.logger.SpongeLogger;
import io.valandur.webapi.sponge.player.SpongePlayerService;
import io.valandur.webapi.sponge.world.SpongeWorldService;
import io.valandur.webapi.world.WorldService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class SpongeWebAPI extends WebAPI<SpongeWebAPI, SpongeWebAPIPlugin> {

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
        return new SpongeHookConfig(plugin);
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
    protected CommandService<SpongeWebAPI> createCommandService() {
        return new SpongeCommandService(this);
    }

    @Override
    protected HookService<SpongeWebAPI> createHookService() {
        return new SpongeHookService(this);
    }

    @Override
    public <T> T runOnMain(Callable<T> callable) throws Exception {
        if (Sponge.server().onMainThread()) {
            return callable.call();
        } else {
            var res = syncExecutor.submit(callable);
            return res.get();
        }
    }

    @Override
    public AsyncTask runAsync(Runnable runnable) {
        var task = Task.builder()
                .plugin(plugin.getContainer())
                .execute(runnable)
                .build();
        var sub = Sponge.asyncScheduler().submit(task);
        return new AsyncTask() {
            @Override
            public void cancel() {
                sub.cancel();
            }

            @Override
            public void await() throws Exception {
                sub.wait();
            }
        };
    }
}
