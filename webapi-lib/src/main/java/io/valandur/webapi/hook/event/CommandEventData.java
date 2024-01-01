package io.valandur.webapi.hook.event;

import io.valandur.webapi.hook.HookEventType;

public record CommandEventData(String cmd) implements EventData {
    @Override
    public HookEventType event() {
        return HookEventType.COMMAND;
    }
}
