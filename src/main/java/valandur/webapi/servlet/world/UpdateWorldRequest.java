package valandur.webapi.servlet.world;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

@JsonDeserialize
public class UpdateWorldRequest extends BaseWorldRequest {

    @JsonDeserialize
    private Boolean loaded;
    public Boolean isLoaded() {
        return loaded;
    }

    @JsonDeserialize
    private Map<String, String> gameRules;
    public Map<String, String> getGameRules() {
        return gameRules;
    }
}
