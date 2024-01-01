package io.valandur.webapi.sponge.hook;

import io.valandur.webapi.hook.HookEventType;
import io.valandur.webapi.hook.HookService;
import io.valandur.webapi.hook.event.CommandEventData;
import io.valandur.webapi.hook.event.PlayerEventData;
import io.valandur.webapi.sponge.SpongeWebAPI;
import io.valandur.webapi.sponge.player.SpongePlayerService;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.ExecuteCommandEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class SpongeHookService extends HookService<SpongeWebAPI> {

    public SpongeHookService(SpongeWebAPI webapi) {
        super(webapi);
    }

    @Listener
    public void onPlayerJoin(final ServerSideConnectionEvent.Join event) {
        var player = ((SpongePlayerService) webapi.getPlayerService()).toPlayer(event.player());
        notifyEventHooks(new PlayerEventData(HookEventType.PLAYER_JOIN, player));
    }

    @Listener
    public void onPlayerLeave(final ServerSideConnectionEvent.Disconnect event) {
        var player = ((SpongePlayerService) webapi.getPlayerService()).toPlayer(event.player());
        notifyEventHooks(new PlayerEventData(HookEventType.PLAYER_LEAVE, player));
    }

    @Listener
    public void onCommand(final ExecuteCommandEvent event) {
        var cmd = event.command();
        notifyEventHooks(new CommandEventData(cmd));
    }
}
