package valandur.webapi.cache.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.text.Text;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedText;
import valandur.webapi.util.Constants;

import static valandur.webapi.command.CommandSource.instance;

@ApiModel("Command")
public class CachedCommand extends CachedObject<CommandMapping> {

    private String name;
    @ApiModelProperty(value = "The name of the command, sometimes also referred to as primary alias.", required = true)
    public String getName() {
        return name;
    }

    private CachedText description;
    @ApiModelProperty(value = "The description provided with the command", required = true)
    public CachedText getDescription() {
        return description;
    }

    private String[] aliases;
    @ApiModelProperty(value = "All the aliases that were registered for this command", required = true)
    public String[] getAliases() {
        return aliases;
    }

    private CachedText usage;
    @ApiModelProperty(value = "A short description of the usage of this command", required = true)
    public CachedText getUsage() {
        return usage;
    }

    private CachedText help;
    @ApiModelProperty(value = "Extended help information on the usage of the command", required = true)
    public CachedText getHelp() {
        return help;
    }


    public CachedCommand(CommandMapping cmd) {
        super(null);

        this.name = cmd.getPrimaryAlias();
        this.aliases = cmd.getAllAliases().toArray(new String[cmd.getAllAliases().size()]);
        try {
            this.usage = new CachedText(cmd.getCallable().getUsage(instance));
            this.description = new CachedText(cmd.getCallable().getShortDescription(instance).orElse(Text.EMPTY));
            this.help = new CachedText(cmd.getCallable().getHelp(instance).orElse(Text.EMPTY));
        } catch (Exception ignored) {}
    }

    @Override
    @JsonIgnore(false)
    public String getLink() {
        return Constants.BASE_PATH + "/cmd/" + name;
    }
}
