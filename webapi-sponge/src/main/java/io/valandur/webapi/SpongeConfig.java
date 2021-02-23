package io.valandur.webapi;

import io.valandur.webapi.config.Config;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;

public class SpongeConfig extends Config {

    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode node;

    public SpongeConfig(String name, Path path) {
        super(name);

        loader = HoconConfigurationLoader.builder().setPath(path).build();
        node = loader.createEmptyNode();
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
        return (T) node.getNode(path).getValue(def);
    }
}
