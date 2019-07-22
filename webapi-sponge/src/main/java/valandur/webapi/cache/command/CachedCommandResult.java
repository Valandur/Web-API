package valandur.webapi.cache.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.command.CommandResult;
import valandur.webapi.cache.CachedObject;

@ApiModel("CommandResult")
public class CachedCommandResult extends CachedObject<CachedCommandResult> {

    private Integer affectedBlocks;
    @ApiModelProperty("The number of blocks that were affected by this command")
    public Integer getAffectedBlocks() {
        return affectedBlocks;
    }

    private Integer affectedEntities;
    @ApiModelProperty("The number of entities that were affected by this command")
    public Integer getAffectedEntities() {
        return affectedEntities;
    }

    private Integer affectedItems;
    @ApiModelProperty("The number of items that were affected by this command")
    public Integer getAffectedItems() {
        return affectedItems;
    }

    private Integer queryResult;
    @ApiModelProperty("The results of the query")
    public Integer getQueryResult() {
        return queryResult;
    }

    private Integer successCount;
    @ApiModelProperty("The success count")
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
