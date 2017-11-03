package valandur.webapi.command.block;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import valandur.webapi.WebAPI;
import valandur.webapi.api.block.IBlockOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CmdBlockUpdatesList implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        List<Text> contents = new ArrayList<>();
        Collection<IBlockOperation> ops = WebAPI.getBlockService().getBlockOperations();
        for (IBlockOperation op : ops) {
            contents.add(Text.builder("[" + op.getType() + "] " + op.getUUID()).color(getColor(op)).build());
        }

        PaginationList.builder().title(Text.of("Block Operations")).contents(contents).sendTo(src);
        return CommandResult.success();
    }

    private TextColor getColor(IBlockOperation op) {
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
