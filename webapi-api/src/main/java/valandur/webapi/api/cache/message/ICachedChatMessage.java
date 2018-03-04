package valandur.webapi.api.cache.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;

@ApiModel("ChatMessage")
public interface ICachedChatMessage extends ICachedMessage {

    @ApiModelProperty(value = "The sender of the message", required = true)
    ICachedPlayer getSender();
}
