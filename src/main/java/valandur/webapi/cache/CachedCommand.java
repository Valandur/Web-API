package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.text.Text;
import valandur.webapi.WebAPI;
import valandur.webapi.misc.WebAPICommandSource;

import java.util.Optional;

public class CachedCommand extends CachedObject {

    @JsonProperty
    public String name;
    @JsonProperty
    public String description;

    public Object[] aliases;
    public String usage;
    public String help;

    public static CachedCommand copyFrom(CommandMapping cmd) {
        CachedCommand cache = new CachedCommand();

        cache.name = cmd.getPrimaryAlias();
        cache.aliases = cmd.getAllAliases().toArray();
        cache.usage = cmd.getCallable().getUsage(WebAPICommandSource.instance).toPlain();
        cache.description = cmd.getCallable().getShortDescription(WebAPICommandSource.instance).orElse(null).toPlain();
        cache.help = cmd.getCallable().getHelp(WebAPICommandSource.instance).orElse(null).toPlain();

        return cache;
    }

    @Override
    public int getCacheDuration() {
        return 0;
    }

    @Override
    public Optional<Object> getLive() {
        return Optional.empty();
    }

    @Override
    @JsonProperty
    public String getLink() {
        return "/api/cmd/" + name;
    }
}
