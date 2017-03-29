package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CachedPlugin extends CachedObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String version;

    public String description;
    public String url;
    public List<String> authors;


    public CachedPlugin(PluginContainer plugin) {
        this.id = plugin.getId();
        this.name = plugin.getName();
        this.description = plugin.getDescription().orElse(null);
        this.version = plugin.getVersion().orElse(null);
        this.url = plugin.getUrl().orElse(null);
        this.authors = new ArrayList<String>(plugin.getAuthors());
    }

    @Override
    public Optional<?> getLive() {
        return Sponge.getPluginManager().getPlugin(id);
    }

    @Override
    @JsonProperty
    public String getLink() {
        return "/api/plugin/" + id;
    }
}
