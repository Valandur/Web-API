package valandur.webapi.cache.plugin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.plugin.meta.PluginDependency;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.plugin.ICachedPluginDependency;

import java.util.Optional;

import static org.spongepowered.plugin.meta.PluginDependency.LoadOrder;

public class CachedPluginDependency extends CachedObject<PluginDependency> implements ICachedPluginDependency {

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
    public Optional<PluginDependency> getLive() {
        return Optional.of(new PluginDependency(loadOrder, id, version, optional));
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
