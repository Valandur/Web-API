package valandur.webapi.cache.command;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.event.command.SendCommandEvent;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.command.ICachedCommandCall;
import valandur.webapi.api.cache.command.ICachedCommandResult;
import valandur.webapi.api.cache.misc.ICachedCause;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.cache.misc.CachedCause;

import java.util.Date;

public class CachedCommandCall extends CachedObject<Object> implements ICachedCommandCall {

    private Long timestamp;
    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    private String command;
    @Override
    public String getCommand() {
        return command;
    }

    private String args;
    @Override
    public String getArgs() {
        return args;
    }

    private boolean cancelled;
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    private ICachedCause cause;
    @Override
    @JsonDetails
    public ICachedCause getCause() {
        return cause;
    }

    private ICachedCommandResult result;
    @Override
    @JsonDetails
    public ICachedCommandResult getResult() {
        return result;
    }


    public CachedCommandCall(SendCommandEvent event, boolean censor) {
        super(null);

        this.timestamp = (new Date()).toInstant().getEpochSecond();
        this.command = censor ? "[censored]" : event.getCommand();
        this.args = censor ? "" : event.getArguments();
        this.cause = new CachedCause(event.getCause());
        this.cancelled = event.isCancelled();
        this.result = new CachedCommandResult(event.getResult());
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
