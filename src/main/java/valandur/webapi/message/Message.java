package valandur.webapi.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import valandur.webapi.api.message.IMessage;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private Map<String, String> options;
    @Override
    public boolean hasOptions() {
        return options != null;
    }
    @Override
    public Map<String, Text> getOptions() {
        return options.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> TextSerializers.FORMATTING_CODE.deserializeUnchecked(e.getValue())
                ));
    }
}
