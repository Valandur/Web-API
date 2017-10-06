package valandur.webapi.cache.command;

import org.spongepowered.api.event.command.SendCommandEvent;
import valandur.webapi.api.cache.command.ICachedCommandCall;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCause;

import java.util.Date;

public class CachedCommandCall extends CachedObject implements ICachedCommandCall {

    private Long timestamp;
    public Long getTimestamp() {
        return timestamp;
    }

    private String command;
    public String getCommand() {
        return command;
    }

    private String args;
    public String getArgs() {
        return args;
    }

    private CachedCause cause;
    public CachedCause getCause() {
        return cause;
    }

    private boolean cancelled;
    public boolean isCancelled() {
        return cancelled;
    }

    private CachedCommandResult result;
    public CachedCommandResult getResult() {
        return result;
    }


    public CachedCommandCall(SendCommandEvent event) {
        super(null);

        this.timestamp = (new Date()).toInstant().getEpochSecond();
        this.command = event.getCommand();
        this.args = event.getArguments();
        this.cause = new CachedCause(event.getCause());
        this.cancelled = event.isCancelled();
        this.result = new CachedCommandResult(event.getResult());
    }
}
