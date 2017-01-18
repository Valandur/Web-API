package valandur.webapi.cache;

import com.google.gson.annotations.Expose;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CachedPlugin extends CachedObject {
    @Expose
    public String id;

    @Expose
    public String name;

    public String description;
    public String version;
    public String url;
    public List<String> authors;

    public static CachedPlugin copyFrom(PluginContainer plugin) {
        CachedPlugin cache = new CachedPlugin();
        cache.id = plugin.getId();
        cache.name = plugin.getName();
        cache.description = plugin.getDescription().isPresent() ? plugin.getDescription().get() : null;
        cache.version = plugin.getVersion().isPresent() ? plugin.getVersion().get() : null;
        cache.url = plugin.getUrl().isPresent() ? plugin.getUrl().get() : null;
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
}
