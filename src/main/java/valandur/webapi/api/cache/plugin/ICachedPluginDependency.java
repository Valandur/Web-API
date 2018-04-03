/*package valandur.webapi.api.cache.plugin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.plugin.meta.PluginDependency;
import valandur.webapi.api.cache.ICachedObject;

@ApiModel("PluginDependency")
public interface ICachedPluginDependency extends ICachedObject<PluginDependency> {

    @ApiModelProperty(value = "The id of the plugin that the original plugin depends on", required = true)
    String getId();

    @ApiModelProperty(value = "The load order of the original plugin in relation to the dependency", required = true)
    PluginDependency.LoadOrder getLoadOrder();

    @ApiModelProperty(value = "The version of the plugin that the original plugin depends on", required = true)
    String getVersion();

    @ApiModelProperty(value = "True if this is an optional dependency, false otherwise", required = true)
    Boolean isOptional();
}
*/