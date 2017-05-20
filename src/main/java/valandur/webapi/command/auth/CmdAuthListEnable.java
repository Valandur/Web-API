package valandur.webapi.command.auth;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import valandur.webapi.WebAPI;

public class CmdAuthListEnable implements CommandExecutor {
    private boolean whitelist = false;

    public CmdAuthListEnable(boolean whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (whitelist) {
            WebAPI.getInstance().getAuthHandler().toggleWhitelist(true);
            src.sendMessage(Text.of("Enabled whitelist"));
        } else {
            WebAPI.getInstance().getAuthHandler().toggleBlacklist(true);
            src.sendMessage(Text.of("Enabled blacklist"));
        }

        return CommandResult.success();
    }
}
