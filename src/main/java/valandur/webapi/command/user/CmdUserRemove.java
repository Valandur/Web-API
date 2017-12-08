package valandur.webapi.command.user;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.user.Users;

import java.util.Optional;

public class CmdUserRemove implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> optUsername = args.getOne("username");
        if (!optUsername.isPresent()) {
            return CommandResult.empty();
        }
        String username = optUsername.get();

        boolean res = Users.removeUser(username);
        if (!res) {
            src.sendMessage(Text.builder("Couldn't find user to remove " + username)
                    .color(TextColors.RED).build());
            return CommandResult.empty();
        }

        src.sendMessage(Text.builder("Removed user ")
                .append(Text.builder(username).color(TextColors.GOLD).build())
                .build());

        return CommandResult.success();
    }
}
