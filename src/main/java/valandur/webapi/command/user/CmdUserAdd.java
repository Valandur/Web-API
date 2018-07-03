package valandur.webapi.command.user;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.WebAPI;
import valandur.webapi.security.SecurityService;
import valandur.webapi.user.UserPermissionStruct;
import valandur.webapi.util.Util;

import java.util.Optional;

public class CmdUserAdd implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) {
        Optional<String> optUsername = args.getOne("username");
        if (!optUsername.isPresent()) {
            return CommandResult.empty();
        }
        String username = optUsername.get();

        Optional<String> optPassword = args.getOne("password");
        String password = optPassword.orElse(Util.generateUniqueId().substring(0, 8));

        Optional<UserPermissionStruct> optUser = WebAPI.getUserService().addUser(
                username, password, SecurityService.permitAllNode());

        if (!optUser.isPresent()) {
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
