package valandur.webapi.command.user;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.user.UserPermission;
import valandur.webapi.user.Users;

import java.util.Optional;

public class CmdUserChangePassword implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<String> optUsername = args.getOne("username");
        if (!optUsername.isPresent()) {
            return CommandResult.empty();
        }
        String username = optUsername.get();

        Optional<String> optPassword = args.getOne("password");
        if (!optPassword.isPresent()) {
            return CommandResult.empty();
        }
        String password = optPassword.get();

        Optional<UserPermission> optUser = Users.getUser(username);
        if (!optUser.isPresent()) {
            src.sendMessage(Text.builder("Could not find user '" + username + "'").color(TextColors.RED).build());
            return CommandResult.empty();
        }

        UserPermission user = optUser.get();

        user.setPassword(Users.hashPassword(password));
        src.sendMessage(Text.builder("Changed password for ")
                .append(Text.builder(username).color(TextColors.GOLD).build())
                .build());

        Users.save();
        return CommandResult.success();
    }
}
