package valandur.webapi.cache;

import com.google.gson.JsonElement;
import org.spongepowered.api.command.CommandResult;
import valandur.webapi.misc.JsonConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CachedCommandCall extends CachedObject {

    public String command;
    public List<String> args;
    public JsonElement source;
    public CachedCommandResult result;

    public static CachedCommandCall copyFrom(String command, String arguments, JsonElement source, CommandResult result) {
        CachedCommandCall call = new CachedCommandCall();
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
