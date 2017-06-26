package valandur.webapi.command.user;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.misc.Util;
import valandur.webapi.user.Users;

import java.util.Optional;

public class CmdUserAdd implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> optUsername = args.getOne("username");
        if (!optUsername.isPresent()) {
            return CommandResult.empty();
        }
        String username = optUsername.get();

        Optional<String> optPassword = args.getOne("password");
        String password = optPassword.orElse(Util.generateUniqueId().substring(0, 8));

        boolean res = Users.addUser(username, password);
        if (!res) {
            src.sendMessage(Text.builder("A user with this name already exists").color(TextColors.RED).build());
            return CommandResult.empty();
        }

        if (!optPassword.isPresent()) {
            src.sendMessage(Text.builder("Created user ")
                    .append(Text.builder(username).color(TextColors.GOLD).build())
                    .append(Text.of(" with password "))
                    .append(Text.builder(password).color(TextColors.GOLD).build())
                    .build());
        } else {
            src.sendMessage(Text.builder("Created user ")
                    .append(Text.builder(username).color(TextColors.GOLD).build())
                    .build());
        }

        return CommandResult.success();
    }
}
