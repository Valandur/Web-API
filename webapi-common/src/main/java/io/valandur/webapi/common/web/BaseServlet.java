package io.valandur.webapi.common.web;

import io.valandur.webapi.WebAPI;
import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.world.WorldService;

public abstract class BaseServlet {

  protected WebAPI<?> webapi;

  protected WorldService<?> worldService;
  protected PlayerService<?> playerService;
  protected EntityService<?> entityService;
  protected InfoService<?> infoService;
  protected ChatService<?> chatService;

  public BaseServlet() {
    webapi = WebAPI.getInstance();

    worldService = webapi.getWorldService();
    playerService = webapi.getPlayerService();
    entityService = webapi.getEntityService();
    infoService = webapi.getServerService();
    chatService = webapi.getChatService();
  }
}
