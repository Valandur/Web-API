package valandur.webapi.cache.command;

import org.spongepowered.api.event.command.SendCommandEvent;
import valandur.webapi.api.cache.command.ICachedCommandCall;
import valandur.webapi.api.cache.command.ICachedCommandResult;
import valandur.webapi.api.cache.misc.ICachedCause;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCause;

import java.util.Date;

public class CachedCommandCall extends CachedObject implements ICachedCommandCall {

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

    private ICachedCause cause;
    @Override
    public ICachedCause getCause() {
        return cause;
    }

    private boolean cancelled;
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    private ICachedCommandResult result;
    @Override
    public ICachedCommandResult getResult() {
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
