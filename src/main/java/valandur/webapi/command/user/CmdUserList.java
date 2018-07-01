package valandur.webapi.command.user;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.WebAPI;
import valandur.webapi.user.UserPermissionStruct;

import java.util.Collection;
import java.util.stream.Collectors;

public class CmdUserList implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Collection<UserPermissionStruct> users = WebAPI.getUserService().getUsers();
        PaginationList.builder()
                .title(Text.of("Web-API users"))
                .contents(users.stream().map(u -> {
                    Text editUser = Text.builder("[Change pw]")
                            .color(TextColors.YELLOW)
                            .onClick(TextActions.suggestCommand("/webapi users changepw " + u.getName() + " "))
                            .build();

                    Text rmvUser = Text.builder(" [Remove]")
                            .color(TextColors.RED)
                            .onClick(TextActions.suggestCommand("/webapi users remove " + u.getName()))
                            .build();

                    return Text.builder(u.getName() + " ").append(editUser, rmvUser).build();
                }).collect(Collectors.toList()))
                .sendTo(src);
        return CommandResult.success();
    }
}
