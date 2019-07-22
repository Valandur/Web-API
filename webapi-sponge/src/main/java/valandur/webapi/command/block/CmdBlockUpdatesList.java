package valandur.webapi.command.block;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.WebAPI;
import valandur.webapi.block.BlockOperation;

import java.util.Collection;
import java.util.stream.Collectors;

import static valandur.webapi.block.BlockOperation.BlockOperationStatus;

public class CmdBlockUpdatesList implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Collection<BlockOperation> ops = WebAPI.getBlockService().getBlockOperations();
        PaginationList.builder()
                .title(Text.of("Web-API Block Ops"))
                .contents(ops.stream().map(op -> {
                    LiteralText.Builder builder = Text.builder(op.getType() + " " + op.getUUID() + " ")
                            .append(Text.builder(op.getStatus() + " ").color(getColor(op)).build());

                    if (op.getStatus() == BlockOperationStatus.RUNNING) {
                        builder.append(
                                Text.builder("[Pause]")
                                        .color(TextColors.YELLOW)
                                        .onClick(TextActions.suggestCommand("/webapi ops pause " + op.getUUID() + " "))
                                        .build()
                        );
                    } else if (op.getStatus() == BlockOperationStatus.PAUSED) {
                        builder.append(
                                Text.builder("[Resume]")
                                        .color(TextColors.YELLOW)
                                        .onClick(TextActions.suggestCommand("/webapi ops pause " + op.getUUID() + " "))
                                        .build()
                        );
                    }

                    if (op.getStatus() == BlockOperationStatus.RUNNING || op.getStatus() == BlockOperationStatus.PAUSED) {
                        builder.append(
                                Text.builder(" [Stop]")
                                        .color(TextColors.RED)
                                        .onClick(TextActions.suggestCommand("/webapi ops stop " + op.getUUID()))
                                        .build()
                        );
                    }

                    return builder.build();
                }).collect(Collectors.toList()))
                .sendTo(src);
        return CommandResult.success();
    }

    private TextColor getColor(BlockOperation op) {
        switch (op.getStatus()) {
            case RUNNING:
                return TextColors.DARK_GREEN;
            case DONE:
                return TextColors.DARK_BLUE;
            case ERRORED:
                return TextColors.DARK_RED;
            case PAUSED:
                return TextColors.YELLOW;
            case CANCELED:
                return TextColors.GRAY;
            default:
                return TextColors.BLACK;
        }
    }
}
