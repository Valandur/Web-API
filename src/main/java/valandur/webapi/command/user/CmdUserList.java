package valandur.webapi.command.user;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import valandur.webapi.user.UserPermission;
import valandur.webapi.user.Users;

import java.util.Collection;
import java.util.stream.Collectors;

public class CmdUserList implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Collection<UserPermission> users = Users.getUsers();
        PaginationList.builder()
                .title(Text.of("Web-API users"))
                .contents(users.stream().map(u -> Text.of(u.getUsername())).collect(Collectors.toList()))
                .sendTo(src);
        return CommandResult.success();
    }
}
