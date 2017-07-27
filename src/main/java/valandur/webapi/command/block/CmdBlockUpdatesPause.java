package valandur.webapi.command.block;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.api.block.IBlockOperation;
import valandur.webapi.block.BlockOperation;

import java.util.Optional;

public class CmdBlockUpdatesPause implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<BlockOperation> update = args.getOne("uuid");
        if (!update.isPresent()) {
            src.sendMessage(Text.builder("Invalid block update uuid!").color(TextColors.DARK_RED).build());
            return CommandResult.empty();
        }

        if (update.get().getStatus() == IBlockOperation.BlockOperationStatus.RUNNING) {
            update.get().pause();
        } else {
            update.get().start();
        }

        return CommandResult.success();
    }
}
