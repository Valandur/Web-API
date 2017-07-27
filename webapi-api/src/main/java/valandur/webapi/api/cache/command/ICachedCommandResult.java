package valandur.webapi.api.cache.command;

import valandur.webapi.api.cache.ICachedObject;

public interface ICachedCommandResult extends ICachedObject {

    Integer getAffectedBlocks();

    Integer getAffectedEntities();

    Integer getAffectedItems();

    Integer getQueryResult();

    Integer getSuccessCount();
}
