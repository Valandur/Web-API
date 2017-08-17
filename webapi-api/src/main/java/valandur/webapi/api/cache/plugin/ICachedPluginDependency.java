package valandur.webapi.api.cache.plugin;

import valandur.webapi.api.cache.ICachedObject;

import static org.spongepowered.plugin.meta.PluginDependency.LoadOrder;

public interface ICachedPluginDependency extends ICachedObject {

    String getId();

    LoadOrder getLoadOrder();

    String getVersion();

    Boolean isOptional();
}
