package valandur.webapi.api.cache.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.cache.ICachedObject;

import java.util.Collection;

@ApiModel(value = "Message", subTypes = { ICachedChatMessage.class })
public interface ICachedMessage extends ICachedObject {

    @ApiModelProperty(value = "The receivers of this message", required = true)
    Collection<Object> getReceivers();

    @ApiModelProperty(value = "The timestamp at which the chat message was sent (epoch millis)", required = true)
    Long getTimestamp();

    @ApiModelProperty(value = "The content of the message", required = true)
    Text getContent();
}
