package valandur.webapi.cache.plugin;

import org.spongepowered.plugin.meta.PluginDependency;
import valandur.webapi.api.cache.CachedObject;

import java.util.Optional;

import static org.spongepowered.plugin.meta.PluginDependency.LoadOrder;

public class CachedPluginDependency extends CachedObject<PluginDependency> {

    private String id;
    public String getId() {
        return null;
    }

    private LoadOrder loadOrder;
    public LoadOrder getLoadOrder() {
        return loadOrder;
    }

    private String version;
    public String getVersion() {
        return version;
    }

    private Boolean optional;
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
    public Optional<PluginDependency> getLive() {
        return Optional.of(new PluginDependency(loadOrder, id, version, optional));
    }
}
