package valandur.webapi.cache.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import valandur.webapi.cache.player.CachedPlayer;

import java.util.Collection;

@ApiModel(value = "ChatMessage", parent = CachedMessage.class)
public class CachedChatMessage extends CachedMessage {

    private CachedPlayer sender;
    @ApiModelProperty(value = "The sender of the message", required = true)
    public CachedPlayer getSender() {
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
