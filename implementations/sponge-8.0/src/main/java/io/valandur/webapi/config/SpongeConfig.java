package io.valandur.webapi.config;

import io.leangen.geantyref.TypeToken;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.security.KeyPermissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.Path;

public class SpongeConfig extends Config {

    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode node;

    public SpongeConfig(String name, Path path) {
        super(name);

        var serializers = Sponge.configManager().serializers().childBuilder()
                .register(TypeToken.get(KeyPermissions.class), new KeyPermissionsSerializer())
                .build();

        var opts = ConfigurationOptions.defaults().serializers(serializers);
        loader = HoconConfigurationLoader.builder().defaultOptions(opts).prettyPrinting(true).path(path).build();
        node = loader.createNode();
    }

    @Override
    public void load() {
        try {
            node = loader.load();
        } catch (IOException e) {
            WebAPI.getInstance().getLogger().error(e.getMessage());
        }
    }

    @Override
    public void save() {
        try {
            loader.save(node);
        } catch (IOException e) {
            WebAPI.getInstance().getLogger().error(e.getMessage());
        }
    }

    @Override
    public <T> T get(String path, TypeToken<T> type, T def) {
        try {
            CommentedConfigurationNode n = node.node(path);
            return (T) n.get(type, def);
        } catch (SerializationException e) {
            return def;
        }
    }

    @Override
    public <T> boolean set(String path, TypeToken<T> type, T value) {
        try {
            CommentedConfigurationNode n = node.node(path);
            n.set(type, value);
            return true;
        } catch (SerializationException e) {
            return false;
        }
    }
}
