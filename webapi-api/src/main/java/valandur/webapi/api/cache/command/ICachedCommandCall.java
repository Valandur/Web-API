package valandur.webapi.api.cache.command;

import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedCause;

public interface ICachedCommandCall extends ICachedObject {

    Long getTimestamp();

    String getCommand();

    String getArgs();

    ICachedCause getCause();

    boolean isCancelled();

    ICachedCommandResult getResult();
}
