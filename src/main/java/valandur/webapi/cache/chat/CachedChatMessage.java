package valandur.webapi.cache.chat;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.message.MessageEvent;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.chat.ICachedChatMessage;
import valandur.webapi.api.cache.player.ICachedPlayer;
import valandur.webapi.cache.CachedObject;

import java.util.Date;

public class CachedChatMessage extends CachedObject implements ICachedChatMessage {

    private Long timestamp;
    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    private ICachedPlayer sender;
    @Override
    public ICachedPlayer getSender() {
        return sender;
    }

    private String message;
    @Override
    public String getMessage() {
        return message;
    }


    public CachedChatMessage(Player sender, MessageEvent event) {
        super(null);

        this.timestamp = (new Date()).toInstant().getEpochSecond();
        this.sender = WebAPI.getCacheService().getPlayer(sender);
        this.message = event.getMessage().toPlain();
    }
}
