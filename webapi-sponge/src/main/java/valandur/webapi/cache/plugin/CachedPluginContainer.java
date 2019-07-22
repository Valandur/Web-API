package valandur.webapi.cache.plugin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@ApiModel("PluginContainer")
public class CachedPluginContainer extends CachedObject<PluginContainer> {

    public enum PluginType {
        Unknown, Sponge, Forge, Minecraft,
    }

    public enum PluginState {
        Loaded, Unloaded, WillBeLoaded, WillBeUnloaded,
    }

    private String id;
    @ApiModelProperty(value = "The unique id of this plugin", required = true)
    public String getId() {
        return id;
    }

    private String name;
    @ApiModelProperty(value = "The name of this plugin", required = true)
    public String getName() {
        return name;
    }

    private String version;
    @ApiModelProperty("The current version of the plugin")
    public String getVersion() {
        return version;
    }

    private String description;
    @ApiModelProperty("A description describing what this plugin does (hopefully)")
    public String getDescription() {
        return description;
    }

    private String url;
    @JsonDetails
    @ApiModelProperty("The url that was added to the plugin (probably the homepage)")
    public String getUrl() {
        return url;
    }

    private List<String> authors;
    @JsonDetails
    @ApiModelProperty(value = "A list of authors that created this plugin", required = true)
    public List<String> getAuthors() {
        return authors;
    }

    private Set<CachedPluginDependency> dependencies = new HashSet<>();
    @JsonDetails
    @ApiModelProperty(value = "Other plugins that this plugin depends on", required = true)
    public Set<CachedPluginDependency> getDependencies() {
        return new HashSet<>(dependencies);
    }

    private PluginType type;
    @ApiModelProperty(value = "The type of the plugin", required = true)
    public PluginType getType() {
        return type;
    }

    private String source;
    @ApiModelProperty("The file source where the plugin was loaded from.")
    public String getSource() {
        return source;
    }

    private PluginState state;
    @ApiModelProperty(value = "The current loaded state of the plugin", required = true)
    public PluginState getState() {
        return state;
    }


    public CachedPluginContainer(PluginContainer plugin) {
        super(plugin);

        this.id = plugin.getId();
        this.name = plugin.getName();
        this.description = plugin.getDescription().orElse(null);
        this.version = plugin.getVersion().orElse(null);
        this.url = plugin.getUrl().orElse(null);
        this.authors = new ArrayList<>(plugin.getAuthors());
        plugin.getDependencies().forEach(d -> this.dependencies.add(new CachedPluginDependency(d)));
        this.source = plugin.getSource().map(p -> p.normalize().toString()).orElse(null);
        this.state = PluginState.Loaded;

        this.checkType();
    }
    public CachedPluginContainer(JsonNode node, Path source) {
        super(null);

        this.id = node.path("modid").asText();
        this.name = node.path("name").asText();
        this.description = node.path("description").asText(null);
        this.version = node.path("version").asText(null);
        this.url = node.path("url").asText(null);
        List<String> authors = new ArrayList<>();
        for (JsonNode authorNode : node.path("authorList")) {
            authors.add(authorNode.asText());
        }
        this.authors = authors;
        Set<CachedPluginDependency> deps = new HashSet<>();
        for (JsonNode depNode : node.path("requiredMods")) {
            deps.add(new CachedPluginDependency(depNode.asText(), true));
        }
        for (JsonNode depNode : node.path("dependencies")) {
            deps.add(new CachedPluginDependency(depNode.asText(), false));
        }
        this.dependencies = deps;
        this.source = source.normalize().toString();
        this.state = source.toString().endsWith(".jar") ? PluginState.WillBeLoaded : PluginState.Unloaded;

        this.checkType();
    }

    private void checkType() {
        if (this.id.equalsIgnoreCase("minecraft") || this.id.equalsIgnoreCase("mcp")) {
            this.type = PluginType.Minecraft;
        } else if (this.id.equalsIgnoreCase("sponge") || this.id.equalsIgnoreCase("spongeapi")) {
            this.type = PluginType.Sponge;
        } else if (this.id.equalsIgnoreCase("fml") || this.id.equalsIgnoreCase("forge")
                || this.id.equalsIgnoreCase("mercurius_updater")) {
            this.type = PluginType.Forge;
        } else if (this.dependencies.stream().anyMatch(d -> d.getId().contains("sponge"))) {
            this.type = PluginType.Sponge;
        } else if (this.dependencies.stream().anyMatch(d -> d.getId().contains("forge"))) {
            this.type = PluginType.Forge;
        } else if (this.dependencies.stream().anyMatch(d -> d.getId().contains("fml"))) {
            this.type = PluginType.Forge;
        } else if (this.source.contains("plugins")) {
            this.type = PluginType.Sponge;
        } else {
            this.type = PluginType.Unknown;
        }
    }

    @JsonIgnore
    public void setWillBeUnloaded() {
        this.state = PluginState.WillBeUnloaded;
        this.source = this.source.replace(".jar", ".jar.disabled");
    }

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public boolean toggle() {
        String newSource = state == PluginState.Loaded || state == PluginState.WillBeLoaded ?
                source.replace(".jar", ".jar.disabled") :
                source.replace(".jar.disabled", ".jar");

        Path oldPath = Paths.get(source).normalize();
        Path newPath = Paths.get(newSource).normalize();
        try {
            Files.move(oldPath, newPath);
        } catch (IOException ignored) {
            return false;
        }

        source = newSource;
        if (state == PluginState.Loaded) {
            state = PluginState.WillBeUnloaded;
        } else if (state == PluginState.WillBeUnloaded) {
            state = PluginState.Loaded;
        } else if (state == PluginState.Unloaded) {
            state = PluginState.WillBeLoaded;
        } else if (state == PluginState.WillBeLoaded) {
            state = PluginState.Unloaded;
        }

        return true;
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
