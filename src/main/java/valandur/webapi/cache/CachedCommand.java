package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.text.Text;
import valandur.webapi.misc.WebAPICommandSource;

public class CachedCommand extends CachedObject {

    @JsonProperty
    public String name;

    @JsonProperty
    public String description;

    public Object[] aliases;
    public String usage;
    public String help;


    public CachedCommand(CommandMapping cmd) {
        this.name = cmd.getPrimaryAlias();
        this.aliases = cmd.getAllAliases().toArray();
        this.usage = cmd.getCallable().getUsage(WebAPICommandSource.instance).toPlain();
        this.description = cmd.getCallable().getShortDescription(WebAPICommandSource.instance).orElse(Text.EMPTY).toPlain();
        this.help = cmd.getCallable().getHelp(WebAPICommandSource.instance).orElse(Text.EMPTY).toPlain();
    }

    @Override
    @JsonProperty
    public String getLink() {
        return "/api/cmd/" + name;
    }
}
