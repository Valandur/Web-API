package valandur.webapi.api.cache.plugin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.cache.ICachedObject;

import java.util.List;
import java.util.Set;

@ApiModel("PluginContainer")
public interface ICachedPluginContainer extends ICachedObject<PluginContainer> {

    enum PluginState {
        Loaded, Unloaded, WillBeLoaded, WillBeUnloaded,
    }

    @ApiModelProperty(value = "The unique id of this plugin", required = true)
    String getId();

    @ApiModelProperty(value = "The name of this plugin", required = true)
    String getName();

    @ApiModelProperty("The current version of the plugin")
    String getVersion();

    @ApiModelProperty("A description describing what this plugin does (hopefully)")
    String getDescription();

    @ApiModelProperty("The url that was added to the plugin (probably the homepage)")
    String getUrl();

    @ApiModelProperty(value = "A list of authors that created this plugin", required = true)
    List<String> getAuthors();

    /*@ApiModelProperty(value = "Other plugins that this plugin depends on", required = true)
    Set<ICachedPluginDependency> getDependencies();*/

    @ApiModelProperty("The file source where the plugin was loaded from.")
    String getSource();

    @ApiModelProperty(value = "The current loaded state of the plugin", required = true)
    PluginState getState();

    @ApiModelProperty(hidden = true)
    boolean toggle();
}
