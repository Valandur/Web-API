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

    public static CachedPlugin copyFrom(PluginContainer plugin) {
        CachedPlugin cache = new CachedPlugin();
        cache.id = plugin.getId();
        cache.name = plugin.getName();
        cache.description = plugin.getDescription().orElse(null);
        cache.version = plugin.getVersion().orElse(null);
        cache.url = plugin.getUrl().orElse(null);
        cache.authors = new ArrayList<String>(plugin.getAuthors());
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return 0;
    }
    @Override
    public Optional<Object> getLive() {
        Optional<PluginContainer> p = Sponge.getPluginManager().getPlugin(id);
        if (!p.isPresent())
            return Optional.empty();
        return Optional.of(p.get());
    }

    @Override
    @JsonProperty
    public String getLink() {
        return "/api/plugin/" + id;
    }
}
