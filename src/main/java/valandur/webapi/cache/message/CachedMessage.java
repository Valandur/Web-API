package valandur.webapi.cache.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.message.ICachedMessage;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CachedMessage extends CachedObject implements ICachedMessage {

    protected Long timestamp;
    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    protected Collection<Object> receivers = new HashSet<>();
    @Override
    public Collection<Object> getReceivers() {
        return receivers;
    }

    protected Text content;
    @Override
    public Text getContent() {
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
        this.content = content.toBuilder().build();
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
