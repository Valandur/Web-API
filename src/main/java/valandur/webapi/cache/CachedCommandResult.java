package valandur.webapi.cache;

import org.spongepowered.api.command.CommandResult;

import java.util.Optional;

public class CachedCommandResult extends CachedObject {

    public Integer affectedBlocks;
    public Integer affectedEntities;
    public Integer affectedItems;
    public Integer queryResult;
    public Integer successCount;

    public static CachedCommandResult copyFrom(CommandResult result) {
        CachedCommandResult res = new CachedCommandResult();
        if (result.getAffectedBlocks().isPresent()) res.affectedBlocks = result.getAffectedBlocks().get();
        if (result.getAffectedEntities().isPresent()) res.affectedEntities = result.getAffectedEntities().get();
        if (result.getAffectedItems().isPresent()) res.affectedItems = result.getAffectedItems().get();
        if (result.getQueryResult().isPresent()) res.queryResult = result.getQueryResult().get();
        if (result.getSuccessCount().isPresent()) res.successCount = result.getSuccessCount().get();
        return res;
    }

    @Override
    public int getCacheDuration() {
        return 0;
    }

    @Override
    public Optional<Object> getLive() {
        return Optional.empty();
    }
}
