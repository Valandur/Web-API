package valandur.webapi.cache.plugin;

import org.spongepowered.plugin.meta.PluginDependency;
import valandur.webapi.api.cache.plugin.ICachedPluginDependency;
import valandur.webapi.cache.CachedObject;

import java.util.Optional;

import static org.spongepowered.plugin.meta.PluginDependency.LoadOrder;

public class CachedPluginDependency extends CachedObject implements ICachedPluginDependency {

    private String id;
    @Override
    public String getId() {
        return null;
    }

    private LoadOrder loadOrder;
    @Override
    public LoadOrder getLoadOrder() {
        return loadOrder;
    }

    private String version;
    @Override
    public String getVersion() {
        return version;
    }

    private Boolean optional;
    @Override
    public Boolean isOptional() {
        return optional;
    }


    public CachedPluginDependency(PluginDependency dependency) {
        super(dependency);

        this.id = dependency.getId();
        this.version = dependency.getVersion();
        this.loadOrder = dependency.getLoadOrder();
        this.optional = dependency.isOptional();
    }


    @Override
    public Optional<?> getLive() {
        return Optional.of(new PluginDependency(loadOrder, id, version, optional));
    }
}
