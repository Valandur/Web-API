package valandur.webapi.api.cache.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedCause;

@ApiModel("CommandCall")
public interface ICachedCommandCall extends ICachedObject<Object> {

    @ApiModelProperty(value = "The timestamp at which the command was executed (epoch millis)", required = true)
    Long getTimestamp();

    @ApiModelProperty(value = "The command that was executed (without arguments)", required = true)
    String getCommand();

    @ApiModelProperty(value = "The arguments that were passed to the command", required = true)
    String getArgs();

    @ApiModelProperty(value = "True if the command was cancelled, false otherwise", required = true)
    boolean isCancelled();

    @ApiModelProperty(value = "The cause of the command execution", required = true)
    ICachedCause getCause();

    @ApiModelProperty(value = "The result of the command execution", required = true)
    ICachedCommandResult getResult();
}
