package io.valandur.webapi.sponge.config;

import io.leangen.geantyref.TypeToken;
import io.valandur.webapi.sponge.SpongeWebAPIPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.util.Collections;
import java.util.Map;

public class SpongeConfig {

    protected final ConfigurationLoader<CommentedConfigurationNode> loader;
    protected CommentedConfigurationNode node;

    public <T> SpongeConfig(SpongeWebAPIPlugin plugin, String confName) {
        this(plugin, confName, Collections.emptyMap());
    }
    public <T> SpongeConfig(SpongeWebAPIPlugin plugin, String confName, Map<TypeToken<T>, TypeSerializer<? super T>> extraSerializers) {
        var builder = Sponge.configManager().serializers().childBuilder();
        for (var entry : extraSerializers.entrySet()) {
            builder.register(entry.getKey(), entry.getValue());
        }
        var serializers = builder.build();
        var path = plugin.getConfigPath().resolve(confName);
        var opts = ConfigurationOptions.defaults().serializers(serializers);
        loader = HoconConfigurationLoader.builder().defaultOptions(opts).prettyPrinting(true).path(path)
                .build();
        node = loader.createNode();
    }

    public void load() throws Exception {
        node = loader.load();
    }

    public void save() throws Exception {
        loader.save(node);
    }

    protected <T> T get(String path, TypeToken<T> type, T def) {
        try {
            CommentedConfigurationNode n = node.node(path);
            return n.get(type, def);
        } catch (SerializationException e) {
            return def;
        }
    }

    protected <T> void set(String path, TypeToken<T> type, T value) {
        try {
            CommentedConfigurationNode n = node.node(path);
            n.set(type, value);
        } catch (SerializationException ignored) {
        }
    }
}
