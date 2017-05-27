package valandur.webapi.cache.command;

import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.text.Text;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.command.CommandSource;

public class CachedCommand extends CachedObject {

    private String name;
    public String getName() {
        return name;
    }

    private String description;
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
        this.usage = cmd.getCallable().getUsage(CommandSource.instance).toPlain();
        this.description = cmd.getCallable().getShortDescription(CommandSource.instance).orElse(Text.EMPTY).toPlain();
        this.help = cmd.getCallable().getHelp(CommandSource.instance).orElse(Text.EMPTY).toPlain();
    }

    @Override
    public String getLink() {
        return "/api/cmd/" + name;
    }
}
