package valandur.webapi.api.cache.chat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.player.ICachedPlayer;

@ApiModel("ChatMessage")
public interface ICachedChatMessage extends ICachedObject {

    @ApiModelProperty(value = "The timestamp at which the chat message was sent", required = true)
    Long getTimestamp();

    @ApiModelProperty(value = "The sender of the message", required = true)
    ICachedPlayer getSender();

    @ApiModelProperty(value = "The content of the message", required = true)
    String getMessage();
}
