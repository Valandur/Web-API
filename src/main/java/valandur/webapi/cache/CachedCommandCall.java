package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.command.CommandResult;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CachedCommandCall extends CachedObject {

    @JsonProperty
    public Long timestamp;

    @JsonProperty
    public String command;

    @JsonProperty
    public List<String> args;

    @JsonProperty
    public JsonNode source;

    @JsonProperty
    public CachedCommandResult result;

    public static CachedCommandCall copyFrom(String command, String arguments, JsonNode source, CommandResult result) {
        CachedCommandCall call = new CachedCommandCall();
        call.timestamp = (new Date()).toInstant().getEpochSecond();
        call.command = command;
        call.args = Arrays.asList(arguments.split(" "));
        call.source = source;
        call.result = CachedCommandResult.copyFrom(result);
        return call;
    }

    @Override
    public int getCacheDuration() {
        return 0;
    }
    @Override
    public Optional<Object> getLive() {
        return Optional.empty();
    }
}
