package valandur.webapi.api.cache.command;

import org.spongepowered.api.command.CommandMapping;
import valandur.webapi.api.cache.ICachedObject;

public interface ICachedCommand extends ICachedObject<CommandMapping> {

    String getName();

    String getDescription();
}
