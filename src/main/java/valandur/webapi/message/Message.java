package valandur.webapi.message;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
    public String getMessage() {
        return message;
    }

    @JsonDeserialize
    private Map<String, String> options;
    public Map<String, String> getOptions() {
        return options;
    }
}
