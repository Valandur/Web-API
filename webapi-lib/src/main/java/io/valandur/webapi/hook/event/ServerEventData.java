package io.valandur.webapi.hook.event;

import io.valandur.webapi.hook.HookEventType;

public record ServerEventData(HookEventType event) implements EventData {
}
