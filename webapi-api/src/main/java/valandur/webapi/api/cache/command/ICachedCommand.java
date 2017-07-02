package valandur.webapi.api.cache.command;

import valandur.webapi.api.cache.ICachedObject;

public interface ICachedCommand extends ICachedObject {

    String getName();

    String getDescription();

    String[] getAliases();

    String getUsage();

    String getHelp();
}
