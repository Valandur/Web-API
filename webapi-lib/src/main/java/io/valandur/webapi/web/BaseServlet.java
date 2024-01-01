package io.valandur.webapi.web;

import io.valandur.webapi.WebAPI;
import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.command.CommandService;
import io.valandur.webapi.command.CommandServlet;
import io.valandur.webapi.entity.EntityService;
import io.valandur.webapi.hook.HookService;
import io.valandur.webapi.player.PlayerService;
import io.valandur.webapi.info.InfoService;
import io.valandur.webapi.world.WorldService;

public abstract class BaseServlet {

  protected WebAPI<?, ?> webapi;

  protected WorldService<?> worldService;
  protected PlayerService<?> playerService;
  protected EntityService<?> entityService;
  protected InfoService<?> infoService;
  protected ChatService<?> chatService;
  protected CommandService<?> commandService;
  protected HookService<?> hookService;

  public BaseServlet() {
    webapi = WebAPI.getInstance();

    worldService = webapi.getWorldService();
    playerService = webapi.getPlayerService();
    entityService = webapi.getEntityService();
    infoService = webapi.getInfoService();
    chatService = webapi.getChatService();
    commandService = webapi.getCommandService();
    hookService = webapi.getHookService();
  }
}
