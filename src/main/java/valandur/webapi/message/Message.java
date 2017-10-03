package valandur.webapi.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import valandur.webapi.api.message.IMessage;
import valandur.webapi.api.message.IMessageOption;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonDeserialize
public class Message implements IMessage {

    @JsonDeserialize
    private String id;
    @Override
    public String getId() {
        return id;
    }

    @JsonDeserialize
    private UUID target;
    @Override
    public UUID getTarget() {
        return target;
    }

    @JsonDeserialize
    private List<UUID> targets;
    @Override
    public List<UUID> getTargets() { return targets; }

    @JsonDeserialize
    private String message;
    @Override
    public Text getMessage() {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(message);
    }

    @JsonDeserialize
    private Boolean once;
    @Override
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
    public List<IMessageOption> getOptions() {
        return new ArrayList<>(options);
    }
}
