package valandur.webapi.cache.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.event.command.SendCommandEvent;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCause;
import valandur.webapi.serialize.JsonDetails;

import java.util.Date;

@ApiModel("CommandCall")
public class CachedCommandCall extends CachedObject<CachedCommandCall> {

    private Long timestamp;
    @ApiModelProperty(value = "The timestamp at which the command was executed (epoch millis)", required = true)
    public Long getTimestamp() {
        return timestamp;
    }

    private String command;
    @ApiModelProperty(value = "The command that was executed (without arguments)", required = true)
    public String getCommand() {
        return command;
    }

    private String args;
    @ApiModelProperty(value = "The arguments that were passed to the command", required = true)
    public String getArgs() {
        return args;
    }

    private boolean cancelled;
    @ApiModelProperty(value = "True if the command was cancelled, false otherwise", required = true)
    public boolean isCancelled() {
        return cancelled;
    }

    private CachedCause cause;
    @JsonDetails
    @ApiModelProperty(value = "The cause of the command execution", required = true)
    public CachedCause getCause() {
        return cause;
    }

    private CachedCommandResult result;
    @JsonDetails
    @ApiModelProperty(value = "The result of the command execution", required = true)
    public CachedCommandResult getResult() {
        return result;
    }


    public CachedCommandCall(SendCommandEvent event, boolean censor) {
        super(null);

        this.timestamp = (new Date()).toInstant().toEpochMilli();
        this.command = censor ? "[censored]" : event.getCommand();
        this.args = censor ? "" : event.getArguments();
        this.cause = new CachedCause(event.getCause());
        this.cancelled = event.isCancelled();
        this.result = new CachedCommandResult(event.getResult());
    }
}
