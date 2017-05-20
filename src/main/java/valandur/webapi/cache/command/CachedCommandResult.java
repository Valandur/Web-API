package valandur.webapi.cache.command;

import org.spongepowered.api.command.CommandResult;
import valandur.webapi.cache.CachedObject;

public class CachedCommandResult extends CachedObject {

    private Integer affectedBlocks;
    public Integer getAffectedBlocks() {
        return affectedBlocks;
    }

    private Integer affectedEntities;
    public Integer getAffectedEntities() {
        return affectedEntities;
    }

    private Integer affectedItems;
    public Integer getAffectedItems() {
        return affectedItems;
    }

    private Integer queryResult;
    public Integer getQueryResult() {
        return queryResult;
    }

    private Integer successCount;
    public Integer getSuccessCount() {
        return successCount;
    }


    public CachedCommandResult(CommandResult result) {
        super(null);

        this.affectedBlocks = result.getAffectedBlocks().orElse(null);
        this.affectedEntities = result.getAffectedEntities().orElse(null);
        this.affectedItems = result.getAffectedItems().orElse(null);
        this.queryResult = result.getQueryResult().orElse(null);
        this.successCount = result.getSuccessCount().orElse(null);
    }
}
