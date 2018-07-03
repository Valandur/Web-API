package valandur.webapi.command.auth;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import valandur.webapi.WebAPI;

public class CmdAuthListRemove implements CommandExecutor {
    private boolean whitelist;

    public CmdAuthListRemove(boolean whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String ip = args.getOne("ip").get().toString();

        if (whitelist) {
            WebAPI.getSecurityService().removeFromWhitelist(ip);
            src.sendMessage(Text.of("Removed " + ip + " from whitelist"));
        } else {
            WebAPI.getSecurityService().removeFromBlacklist(ip);
            src.sendMessage(Text.of("Removed " + ip + " from blacklist"));
        }

        return CommandResult.success();
    }
}
