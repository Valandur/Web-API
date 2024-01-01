package io.valandur.webapi.hook.event;

import io.valandur.webapi.chat.ChatHistoryItem;
import io.valandur.webapi.hook.HookEventType;

public record ChatEventData(ChatHistoryItem message) implements EventData {
    @Override
    public HookEventType event() {
        return HookEventType.CHAT;
    }
}
