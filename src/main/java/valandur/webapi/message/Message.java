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
    public String getId() {
        return id;
    }

    @JsonDeserialize
    private UUID target;
    public UUID getTarget() {
        return target;
    }

    @JsonDeserialize
    private String message;
    public Text getMessage() {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(message);
    }

    @JsonDeserialize
    private boolean once;
    public boolean isOnce() {
        return once;
    }

    @JsonDeserialize
    private Map<String, String> options;
    public Map<String, Text> getOptions() {
        return options.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> TextSerializers.FORMATTING_CODE.deserializeUnchecked(e.getValue())
                ));
    }
}
