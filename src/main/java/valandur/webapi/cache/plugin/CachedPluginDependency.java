package valandur.webapi.cache.plugin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.plugin.meta.PluginDependency;
import valandur.webapi.api.cache.CachedObject;

import java.util.Optional;

import static org.spongepowered.plugin.meta.PluginDependency.LoadOrder;

@ApiModel(value = "PluginDependency")
public class CachedPluginDependency extends CachedObject<PluginDependency> {

    private String id;
    @ApiModelProperty(value = "The id of the plugin that the original plugin depends on")
    public String getId() {
        return null;
    }

    private LoadOrder loadOrder;
    @ApiModelProperty(value = "The of the original plugin in relation to the dependency")
    public LoadOrder getLoadOrder() {
        return loadOrder;
    }

    private String version;
    @ApiModelProperty(value = "The version of the plugin that the original plugin depends on")
    public String getVersion() {
        return version;
    }

    private Boolean optional;
    @ApiModelProperty(value = "True if this is an optional dependency, false otherwise")
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
