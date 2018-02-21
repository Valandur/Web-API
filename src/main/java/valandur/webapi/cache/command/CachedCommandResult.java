package valandur.webapi.cache.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.command.CommandResult;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.command.ICachedCommandResult;

public class CachedCommandResult extends CachedObject<Object> implements ICachedCommandResult {

    private Integer affectedBlocks;
    @Override
    public Integer getAffectedBlocks() {
        return affectedBlocks;
    }

    private Integer affectedEntities;
    @Override
    public Integer getAffectedEntities() {
        return affectedEntities;
    }

    private Integer affectedItems;
    @Override
    public Integer getAffectedItems() {
        return affectedItems;
    }

    private Integer queryResult;
    @Override
    public Integer getQueryResult() {
        return queryResult;
    }

    private Integer successCount;
    @Override
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

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
