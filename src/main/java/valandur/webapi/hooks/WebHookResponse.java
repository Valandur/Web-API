package valandur.webapi.hooks;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class WebHookResponse {

    @JsonDeserialize
    private String message;
    public String getMessage() {
        return message;
    }

    @JsonDeserialize
    private String[] targets;
    public String[] getTargets() {
        return targets;
    }
}
