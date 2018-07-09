package valandur.webapi.integration.gwmcrates;

import org.gwmdevelopments.sponge_plugin.crates.drop.drops.CommandsDrop;
import valandur.webapi.cache.CachedObject;

public class CachedExecCommand extends CachedObject<CommandsDrop.ExecutableCommand> {

    private String command;
    public String getCommand() {
        return command;
    }

    private boolean console;
    public boolean isConsole() {
        return console;
    }


    public CachedExecCommand(CommandsDrop.ExecutableCommand value) {
        super(value);

        this.command = value.getCommand();
        this.console = value.isConsole();
    }
}
