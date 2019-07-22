package valandur.webapi.command.block;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.block.BlockOperation;

import java.util.Optional;

public class CmdBlockUpdatesStop implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<BlockOperation> op = args.getOne("uuid");
        if (!op.isPresent()) {
            src.sendMessage(Text.builder("Invalid block operation uuid!")
                    .color(TextColors.DARK_RED)
                    .build());
            return CommandResult.empty();
        }

        op.get().stop(null);
        src.sendMessage(Text.builder("Successfully cancelled block operation")
                .color(TextColors.DARK_GREEN)
                .build());
        return CommandResult.success();
    }
}
