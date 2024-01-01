package io.valandur.webapi.chat;

import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;
import java.util.ArrayList;
import java.util.List;

public abstract class ChatService<T extends WebAPI<?, ?>> extends Service<T> {

  protected List<ChatHistoryItem> chatHistory;

  public ChatService(T webapi) {
    super(webapi);

    chatHistory = new ArrayList<>();
  }

  public List<ChatHistoryItem> getChatHistory() {
    return chatHistory;
  }

  public abstract void sendChatMessage(String message);
}
