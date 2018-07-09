package valandur.webapi.cache.message;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedText;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

@ApiModel(value = "Message", subTypes = { CachedChatMessage.class })
public class CachedMessage extends CachedObject {

    protected Long timestamp;
    @ApiModelProperty(value = "The timestamp at which the chat message was sent (epoch millis)", required = true)
    public Long getTimestamp() {
        return timestamp;
    }

    protected Collection<Object> receivers = new HashSet<>();
    @ApiModelProperty(value = "The receivers of this message", required = true)
    public Collection<Object> getReceivers() {
        return receivers;
    }

    protected CachedText content;
    @ApiModelProperty(value = "The content of the message", required = true)
    public CachedText getContent() {
        return content;
    }


    public CachedMessage(Collection<MessageReceiver> receivers, Text content) {
        super(null);

        this.timestamp = (new Date()).toInstant().toEpochMilli();
        if (receivers != null) {
            this.receivers.addAll(receivers.stream()
                    .map(m -> cacheService.asCachedObject(m))
                    .collect(Collectors.toList()));
        }
        this.content = new CachedText(content);
    }
}
