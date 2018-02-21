package valandur.webapi.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.message.IMessage;
import valandur.webapi.api.message.IMessageOption;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Message extends CachedObject<IMessage> implements IMessage {

    private UUID uuid;
    @Override
    public UUID getUUID() {
        return uuid;
    }

    private String id;
    @Override
    public String getId() {
        return id;
    }

    private UUID target;
    @Override
    @JsonDetails
    public UUID getTarget() {
        return target;
    }

    private List<UUID> targets;
    @Override
    @JsonDetails
    public List<UUID> getTargets() { return targets; }

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

    private Boolean once;
    @Override
    @JsonDetails
    public Boolean isOnce() {
        return once;
    }

    private List<MessageOption> options;
    @Override
    @JsonDetails
    public List<IMessageOption> getOptions() {
        return new ArrayList<>(options);
    }

    @Override
    @JsonIgnore
    public boolean hasOptions() {
        return options != null;
    }




    public Message() {
        super(null);

        this.uuid = UUID.randomUUID();
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/message/" + uuid;
    }

    @Override
    public Optional<IMessage> getLive() {
        return super.getLive();
    }
}
