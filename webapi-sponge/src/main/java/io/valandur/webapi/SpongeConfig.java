package io.valandur.webapi;

import io.valandur.webapi.config.Config;
import org.spongepowered.configurate.CommentedConfigurationNode;
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

        loader = HoconConfigurationLoader.builder().path(path).build();
        node = loader.createNode();
    }

    @Override
    public void load() {
        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save() {
        try {
            loader.save(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T get(String path, T def) {
        try {
            CommentedConfigurationNode n = node.node(path);
            return (T) n.get(def.getClass(), def);
        } catch (SerializationException e) {
            return def;
        }
    }
}
