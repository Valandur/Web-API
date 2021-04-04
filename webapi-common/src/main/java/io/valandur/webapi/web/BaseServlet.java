package io.valandur.webapi.web;

import io.valandur.webapi.WebAPI;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.world.WorldService;

public abstract class BaseServlet {

    protected WebAPI<?> webapi;
    protected WorldService worldService;
    protected PlayerService playerService;

    public BaseServlet() {
        this.webapi = WebAPI.getInstance();
        this.worldService = this.webapi.getWorldService();
        this.playerService = this.webapi.getPlayerService();
    }
}
