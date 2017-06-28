package valandur.webapi.api.cache.chat;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.message.MessageEvent;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.player.CachedPlayer;

import java.util.Date;

public class CachedChatMessage extends CachedObject {

    private Long timestamp;
    public Long getTimestamp() {
        return timestamp;
    }

    private CachedPlayer sender;
    public CachedPlayer getSender() {
        return sender;
    }

    private String message;
    public String getMessage() {
        return message;
    }


    public CachedChatMessage(Player sender, MessageEvent event) {
        super(null);

        this.timestamp = (new Date()).toInstant().getEpochSecond();
        WebAPIAPI.getCacheService().ifPresent(srv -> this.sender = srv.getPlayer(sender));
        this.message = event.getMessage().toPlain();
    }
}
