package valandur.webapi.cache.command;

import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.cache.command.ICachedCommand;
import valandur.webapi.cache.CachedObject;

public class CachedCommand extends CachedObject implements ICachedCommand {

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


    public CachedCommand(CommandMapping cmd, CommandSource source) {
        super(null);

        this.name = cmd.getPrimaryAlias();
        this.aliases = cmd.getAllAliases().toArray(new String[cmd.getAllAliases().size()]);
        this.usage = cmd.getCallable().getUsage(source).toPlain();
        this.description = cmd.getCallable().getShortDescription(source).orElse(Text.EMPTY).toPlain();
        this.help = cmd.getCallable().getHelp(source).orElse(Text.EMPTY).toPlain();
    }

    @Override
    public String getLink() {
        return "/api/cmd/" + name;
    }
}
