package io.valandur.webapi.fabric.chat;

import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.fabric.FabricWebAPI;

public class FabricChatService extends ChatService<FabricWebAPI> {

  public FabricChatService(FabricWebAPI webapi) {
    super(webapi);
  }

  @Override
  public void sendChatMessage(String message) {
    // TODO
  }
}
