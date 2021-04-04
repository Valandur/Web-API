package io.valandur.webapi.web;

import io.valandur.webapi.WebAPI;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.server.ServerService;
import io.valandur.webapi.world.WorldService;

public abstract class BaseServlet {

    protected WebAPI<?, ?> webapi;

    protected WorldService<?> worldService;
    protected PlayerService<?> playerService;
    protected ServerService<?> serverService;

    public BaseServlet() {
        webapi = WebAPI.getInstance();

        worldService = webapi.getWorldService();
        playerService = webapi.getPlayerService();
        serverService = webapi.getServerService();
    }
}
