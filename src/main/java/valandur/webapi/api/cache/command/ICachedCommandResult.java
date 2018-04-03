package valandur.webapi.api.cache.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.cache.ICachedObject;

@ApiModel("CommandResult")
public interface ICachedCommandResult extends ICachedObject<Object> {

    @ApiModelProperty("The number of blocks that were affected by this command")
    Integer getAffectedBlocks();

    @ApiModelProperty("The number of entities that were affected by this command")
    Integer getAffectedEntities();

    @ApiModelProperty("The number of items that were affected by this command")
    Integer getAffectedItems();

    @ApiModelProperty("The results of the query")
    Integer getQueryResult();

    @ApiModelProperty("The success count")
    Integer getSuccessCount();
}
