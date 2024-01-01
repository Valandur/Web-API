package io.valandur.webapi.hook.event;

import io.valandur.webapi.hook.HookEventType;
import io.valandur.webapi.player.Player;

public record PlayerEventData(HookEventType event, Player player) implements EventData {
}
