package valandur.webapi.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Map;
import java.util.UUID;

@JsonDeserialize
public class Message {

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
    private Map<String, String> options;
    public Map<String, String> getOptions() {
        return options;
    }
}
