package valandur.webapi.cache.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.text.Text;
import valandur.webapi.Constants;
import valandur.webapi.cache.CachedObject;

import static valandur.webapi.command.CommandSource.instance;

@ApiModel("Command")
public class CachedCommand extends CachedObject<CommandMapping> {

    private String name;
    @ApiModelProperty(value = "The name of the command, sometimes also referred to as primary alias.", required = true)
    public String getName() {
        return name;
    }

    private Text description;
    @ApiModelProperty(value = "The description provided with the command", required = true)
    public Text getDescription() {
        return description;
    }

    private String[] aliases;
    @ApiModelProperty(value = "All the aliases that were registered for this command", required = true)
    public String[] getAliases() {
        return aliases;
    }

    private Text usage;
    @ApiModelProperty(value = "A short description of the usage of this command", required = true)
    public Text getUsage() {
        return usage;
    }

    private Text help;
    @ApiModelProperty(value = "Extended help information on the usage of the command", required = true)
    public Text getHelp() {
        return help;
    }


    public CachedCommand(CommandMapping cmd) {
        super(null);

        this.name = cmd.getPrimaryAlias();
        this.aliases = cmd.getAllAliases().toArray(new String[cmd.getAllAliases().size()]);
        try {
            this.usage = cmd.getCallable().getUsage(instance).toBuilder().build();
            this.description = cmd.getCallable().getShortDescription(instance).orElse(Text.EMPTY).toBuilder().build();
            this.help = cmd.getCallable().getHelp(instance).orElse(Text.EMPTY).toBuilder().build();
        } catch (Exception ignored) {}
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/cmd/" + name;
    }
}
