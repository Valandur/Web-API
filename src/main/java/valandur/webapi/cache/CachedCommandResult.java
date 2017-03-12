package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.spongepowered.api.command.CommandResult;

public class CachedCommandResult extends CachedObject {

    @JsonProperty
    public Integer affectedBlocks;

    @JsonProperty
    public Integer affectedEntities;

    @JsonProperty
    public Integer affectedItems;

    @JsonProperty
    public Integer queryResult;

    @JsonProperty
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
}
