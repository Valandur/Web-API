/*package valandur.webapi.cache.plugin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.plugin.meta.PluginDependency;
import valandur.webapi.cache.CachedObject;

import java.util.Optional;

import static org.spongepowered.plugin.meta.PluginDependency.LoadOrder;

@ApiModel("PluginDependency")
public class CachedPluginDependency extends CachedObject<PluginDependency> {

    private String id;
    @ApiModelProperty(value = "The id of the plugin that the original plugin depends on", required = true)
    public String getId() {
        return id;
    }

    private LoadOrder loadOrder;
    @ApiModelProperty(value = "The load order of the original plugin in relation to the dependency", required = true)
    public LoadOrder getLoadOrder() {
        return loadOrder;
    }

    private String version;
    @ApiModelProperty(value = "The version of the plugin that the original plugin depends on", required = true)
    public String getVersion() {
        return version;
    }

    private Boolean optional;
    @ApiModelProperty(value = "True if this is an optional dependency, false otherwise", required = true)
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
    public CachedPluginDependency(String id, boolean optional) {
        super(null);

        this.id = id;
        this.optional = optional;
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
*/