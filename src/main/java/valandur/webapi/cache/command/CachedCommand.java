package valandur.webapi.cache.command;

import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.command.ICachedCommand;
import valandur.webapi.util.Constants;

import static valandur.webapi.command.CommandSource.instance;

public class CachedCommand extends CachedObject<CommandMapping> implements ICachedCommand {

    private String name;
    @Override
    public String getName() {
        return name;
    }

    private String description;
    @Override
    public String getDescription() {
        return description;
    }

    private String[] aliases;
    public String[] getAliases() {
        return aliases;
    }

    private String usage;
    public String getUsage() {
        return usage;
    }

    private String help;
    public String getHelp() {
        return help;
    }


    public CachedCommand(CommandMapping cmd) {
        super(null);

        this.name = cmd.getPrimaryAlias();
        this.aliases = cmd.getAllAliases().toArray(new String[cmd.getAllAliases().size()]);
        try {
            this.usage = cmd.getCallable().getUsage(instance).toPlain();
            this.description = cmd.getCallable().getShortDescription(instance).orElse(Text.EMPTY).toPlain();
            this.help = cmd.getCallable().getHelp(instance).orElse(Text.EMPTY).toPlain();
        } catch (Exception ignored) {}
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/cmd/" + name;
    }
}
