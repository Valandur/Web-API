package valandur.webapi.cache.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import valandur.webapi.api.cache.message.ICachedChatMessage;
import valandur.webapi.api.cache.player.ICachedPlayer;

import java.util.Collection;

public class CachedChatMessage extends CachedMessage implements ICachedChatMessage {

    private ICachedPlayer sender;
    @Override
    public ICachedPlayer getSender() {
        return sender;
    }


    public CachedChatMessage(Player sender, Collection<MessageReceiver> receivers, Text content) {
        super(receivers, content);

        this.sender = cacheService.getPlayer(sender);
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
