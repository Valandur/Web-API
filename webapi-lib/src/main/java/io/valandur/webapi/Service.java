package io.valandur.webapi;

import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.command.CommandService;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.hook.HookService;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.logger.Logger;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.world.WorldService;

public abstract class Service<T extends WebAPI<?, ?>> {

    protected T webapi;

    protected WorldService<?> worldService;
    protected PlayerService<?> playerService;
    protected EntityService<?> entityService;
    protected InfoService<?> infoService;
    protected ChatService<?> chatService;
    protected CommandService<?> commandService;
    protected HookService<?> hookService;
    protected Logger logger;

    public Service(T webapi) {
        this.webapi = webapi;
        logger = webapi.getLogger();
    }

    public void init() {
        worldService = webapi.getWorldService();
        playerService = webapi.getPlayerService();
        entityService = webapi.getEntityService();
        infoService = webapi.getInfoService();
        chatService = webapi.getChatService();
        commandService = webapi.getCommandService();
        hookService = webapi.getHookService();
    }

    public void start() {

    }

    public void stop() {

    }
}
