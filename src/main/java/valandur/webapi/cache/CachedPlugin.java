package valandur.webapi.cache;

import com.google.gson.annotations.Expose;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.List;

public class CachedPlugin {
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
}
