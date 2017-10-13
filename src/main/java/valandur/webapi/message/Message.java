package valandur.webapi.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.message.IMessage;
import valandur.webapi.api.message.IMessageOption;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@JsonDeserialize
public class Message extends CachedObject<IMessage> implements IMessage {

    private UUID uuid;
    @Override
    public UUID getUUID() {
        return uuid;
    }

    @JsonDeserialize
    private String id;

    @Override
    public String getId() {
        return id;
    }

    @JsonDeserialize
    private UUID target;
    @Override
    @JsonDetails
    public UUID getTarget() {
        return target;
    }

    @JsonDeserialize
    private List<UUID> targets;
    @Override
    @JsonDetails
    public List<UUID> getTargets() { return targets; }

    @JsonDeserialize
    private String message;
    @Override
    @JsonIgnore
    public Text getMessage() {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(message);
    }
    @JsonProperty("message")
    @JsonDetails
    public String getRawMessage() {
        return message;
    }

    @JsonDeserialize
    private Boolean once;
    @Override
    @JsonDetails
    public Boolean isOnce() {
        return once;
    }

    @JsonDeserialize
    private List<MessageOption> options;
    @Override
    public boolean hasOptions() {
        return options != null;
    }
    @Override
    @JsonDetails
    public List<IMessageOption> getOptions() {
        return new ArrayList<>(options);
    }


    public Message() {
        super(null);

        this.uuid = UUID.randomUUID();
    }

    @Override
    public String getLink() {
        return "/api/message/" + uuid;
    }

    @Override
    public Optional<IMessage> getLive() {
        return super.getLive();
    }
}
