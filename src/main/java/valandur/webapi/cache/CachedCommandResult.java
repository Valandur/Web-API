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


    public CachedCommandResult(CommandResult result) {
        if (result.getAffectedBlocks().isPresent()) this.affectedBlocks = result.getAffectedBlocks().get();
        if (result.getAffectedEntities().isPresent()) this.affectedEntities = result.getAffectedEntities().get();
        if (result.getAffectedItems().isPresent()) this.affectedItems = result.getAffectedItems().get();
        if (result.getQueryResult().isPresent()) this.queryResult = result.getQueryResult().get();
        if (result.getSuccessCount().isPresent()) this.successCount = result.getSuccessCount().get();
    }
}
