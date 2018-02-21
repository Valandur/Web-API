package valandur.webapi.cache.command;

import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.text.Text;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.command.ICachedCommand;
import valandur.webapi.util.Constants;

import java.net.URI;

import static valandur.webapi.command.CommandSource.instance;

public class CachedCommand extends CachedObject<CommandMapping> implements ICachedCommand {

    private String name;
    @Override
    public String getName() {
        return name;
    }

    private Text description;
    @Override
    public Text getDescription() {
        return description;
    }

    private String[] aliases;
    public String[] getAliases() {
        return aliases;
    }

    private Text usage;
    public Text getUsage() {
        return usage;
    }

    private Text help;
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
