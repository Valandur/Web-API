package valandur.webapi.api.cache.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.cache.player.ICachedPlayer;

@ApiModel(value = "ChatMessage", parent = ICachedMessage.class)
public interface ICachedChatMessage extends ICachedMessage {

    @ApiModelProperty(value = "The sender of the message", required = true)
    ICachedPlayer getSender();
}
