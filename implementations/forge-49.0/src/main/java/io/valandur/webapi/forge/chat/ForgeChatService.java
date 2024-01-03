package io.valandur.webapi.forge.chat;

import io.valandur.webapi.chat.ChatService;
import io.valandur.webapi.forge.ForgeWebAPI;

public class ForgeChatService extends ChatService<ForgeWebAPI> {

    public ForgeChatService(ForgeWebAPI webapi) {
        super(webapi);
    }

    @Override
    public void sendChatMessage(String message) {

    }
}
