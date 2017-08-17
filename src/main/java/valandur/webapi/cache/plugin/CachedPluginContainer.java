package valandur.webapi.cache.plugin;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import valandur.webapi.api.cache.plugin.ICachedPluginContainer;
import valandur.webapi.api.cache.plugin.ICachedPluginDependency;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.servlet.plugin.CachedPluginDependency;

import java.util.*;

public class CachedPluginContainer extends CachedObject implements ICachedPluginContainer {

    private String id;
    public String getId() {
        return id;
    }

    private String name;
    public String getName() {
        return name;
    }

    private String version;
    public String getVersion() {
        return version;
    }

    private String description;
    public String getDescription() {
        return description;
    }

    private String url;
    public String getUrl() {
        return url;
    }

    private List<String> authors;
    public List<String> getAuthors() {
        return authors;
    }

    private Set<CachedPluginDependency> dependencies = new HashSet<>();
    public Set<ICachedPluginDependency> getDependencies() {
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
    public Optional<?> getLive() {
        return Sponge.getPluginManager().getPlugin(id);
    }

    @Override
    public String getLink() {
        return "/api/plugin/" + id;
    }
}
