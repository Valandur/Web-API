package valandur.webapi.api.cache.command;

import org.spongepowered.api.event.cause.Cause;
import valandur.webapi.api.cache.ICachedObject;

public interface ICachedCommandCall extends ICachedObject {

    Long getTimestamp();

    String getCommand();

    String getArgs();

    Cause getCause();

    boolean isCancelled();

    ICachedCommandResult getResult();
}
