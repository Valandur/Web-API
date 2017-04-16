package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.event.command.SendCommandEvent;
import valandur.webapi.json.JsonConverter;

import java.util.Date;

public class CachedCommandCall extends CachedObject {

    @JsonProperty
    public Long timestamp;

    @JsonProperty
    public String command;

    @JsonProperty
    public String args;

    @JsonProperty
    public JsonNode cause;

    @JsonProperty
    public boolean wasCancelled;

    @JsonProperty
    public CachedCommandResult result;


    public CachedCommandCall(SendCommandEvent event) {
        this.timestamp = (new Date()).toInstant().getEpochSecond();
        this.command = event.getCommand();
        this.args = event.getArguments();
        this.cause = JsonConverter.toJson(event.getCause());
        this.wasCancelled = event.isCancelled();
        this.result = new CachedCommandResult(event.getResult());
    }
}
