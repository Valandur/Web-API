package valandur.webapi.api.cache.plugin;

import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.cache.ICachedObject;

public interface ICachedPluginContainer extends ICachedObject<PluginContainer> {

    String getId();

    String getName();

    String getVersion();

    String getDescription();

    String getUrl();
}
