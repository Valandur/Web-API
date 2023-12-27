package io.valandur.webapi.chat;

import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;
import java.util.ArrayList;
import java.util.List;

public abstract class ChatService<T extends WebAPI<?>> extends Service<T> {

  protected List<ChatMessage> chatMessages;

  public ChatService(T webapi) {
    super(webapi);

    chatMessages = new ArrayList<>();
  }

  public List<ChatMessage> getChatMessages() {
    return chatMessages;
  }

  public abstract void sendChatMessage(String message);
}
