package valandur.webapi.command.auth;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import valandur.webapi.WebAPI;

public class CmdAuthListAdd implements CommandExecutor {
    private boolean whitelist = false;

    public CmdAuthListAdd(boolean whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String ip = args.getOne("ip").get().toString();

        if (whitelist) {
            WebAPI.getInstance().getAuthHandler().addToWhitelist(ip);
            src.sendMessage(Text.of("Added " + ip + " to whitelist"));
        } else {
            WebAPI.getInstance().getAuthHandler().addToBlacklist(ip);
            src.sendMessage(Text.of("Added " + ip + " to blacklist"));
        }

        return CommandResult.success();
    }
}
