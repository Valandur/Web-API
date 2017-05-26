package valandur.webapi.command.blocks;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import valandur.webapi.block.BlockUpdate;
import valandur.webapi.block.Blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CmdBlockUpdatesList implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        List<Text> contents = new ArrayList<>();
        Collection<BlockUpdate> blocks = Blocks.getBlockUpdates();
        for (BlockUpdate update : blocks) {
            contents.add(Text.of(update.getUUID()));
        }

        PaginationList.builder().title(Text.of("Running Block Updates")).contents(contents).sendTo(src);
        return CommandResult.success();
    }
}
