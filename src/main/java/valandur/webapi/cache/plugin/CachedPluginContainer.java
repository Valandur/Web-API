package valandur.webapi.cache.plugin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.plugin.ICachedPluginContainer;
import valandur.webapi.util.Constants;

import java.util.*;

@ApiModel(value = "PluginContainer")
public class CachedPluginContainer extends CachedObject<PluginContainer> implements ICachedPluginContainer {

    private String id;
    @Override
    @ApiModelProperty(value = "The unique id of the plugin")
    public String getId() {
        return id;
    }

    private String name;
    @Override
    @ApiModelProperty(value = "The name of the plugin")
    public String getName() {
        return name;
    }

    private String version;
    @Override
    @ApiModelProperty(value = "The current version of the plugin")
    public String getVersion() {
        return version;
    }

    private String description;
    @Override
    @ApiModelProperty(value = "The plugin description")
    public String getDescription() {
        return description;
    }

    private String url;
    @Override
    @ApiModelProperty(value = "The homepage URL of the plugin")
    public String getUrl() {
        return url;
    }

    private List<String> authors;
    @ApiModelProperty(value = "A list of authors that created this plugin")
    public List<String> getAuthors() {
        return authors;
    }

    private Set<CachedPluginDependency> dependencies = new HashSet<>();
    @ApiModelProperty(value = "Other plugins that this plugin depends on")
    public Set<CachedPluginDependency> getDependencies() {
        return new HashSet<>(dependencies);
    }


    public CachedPluginContainer(PluginContainer plugin) {
        super(plugin);

        this.id = plugin.getId();
        this.name = plugin.getName();
        this.description = plugin.getDescription().orElse(null);
        this.version = plugin.getVersion().orElse(null);
        this.url = plugin.getUrl().orElse(null);
        this.authors = new ArrayList<>(plugin.getAuthors());
        plugin.getDependencies().forEach(d -> dependencies.add(new CachedPluginDependency(d)));
    }

    @Override
    public Optional<PluginContainer> getLive() {
        return Sponge.getPluginManager().getPlugin(id);
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/plugin/" + id;
    }
}
