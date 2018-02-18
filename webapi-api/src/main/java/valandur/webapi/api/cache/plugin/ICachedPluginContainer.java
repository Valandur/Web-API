package valandur.webapi.api.cache.plugin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.cache.ICachedObject;

import java.util.List;
import java.util.Set;

@ApiModel("PluginContainer")
public interface ICachedPluginContainer extends ICachedObject<PluginContainer> {

    @ApiModelProperty(value = "The unique id of this plugin", required = true)
    String getId();

    @ApiModelProperty(value = "The name of this plugin", required = true)
    String getName();

    @ApiModelProperty(value = "The current version of the plugin", required = true)
    String getVersion();

    @ApiModelProperty(value = "A description describing what this plugin does (hopefully)", required = true)
    String getDescription();

    @ApiModelProperty("The url that was added to the plugin (probably the homepage)")
    String getUrl();

    @ApiModelProperty(value = "A list of authors that created this plugin")
    List<String> getAuthors();

    @ApiModelProperty(value = "Other plugins that this plugin depends on")
    Set<ICachedPluginDependency> getDependencies();
}
