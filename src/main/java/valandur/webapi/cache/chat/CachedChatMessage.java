package valandur.webapi.cache.chat;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.message.MessageEvent;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.player.CachedPlayer;
import valandur.webapi.cache.DataCache;

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
        this.sender = DataCache.getPlayer(sender.getUniqueId()).orElse(null);
        this.message = event.getMessage().toPlain();
    }
}
