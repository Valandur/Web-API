package io.valandur.webapi.spigot.chat;

import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.spigot.SpigotWebAPI;

public class SpigotChatService extends ChatService<SpigotWebAPI> {

  public SpigotChatService(SpigotWebAPI webapi) {
    super(webapi);
  }

  @Override
  public void sendChatMessage(String message) {
    // TODO
  }
}
