package valandur.webapi.command.blocks;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.block.BlockUpdate;

import java.util.Optional;

public class CmdBlockUpdatesStop implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Optional<BlockUpdate> update = args.getOne("uuid");
        if (!update.isPresent()) {
            src.sendMessage(Text.builder("Invalid block update uuid!").color(TextColors.DARK_RED).build());
            return CommandResult.empty();
        }

        update.get().stop("Cancelled by " + src.getName());
        src.sendMessage(Text.builder("Successfully cancelled block update").color(TextColors.DARK_GREEN).build());
        return CommandResult.success();
    }
}
