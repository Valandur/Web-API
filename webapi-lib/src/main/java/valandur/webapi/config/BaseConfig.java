package valandur.webapi.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.io.IOException;
import java.nio.file.Path;

@ConfigSerializable
public class BaseConfig {

    private CommentedConfigurationNode node;
    public void setNode(CommentedConfigurationNode node) {
        this.node = node;
    }

    private ConfigurationLoader<CommentedConfigurationNode> loader;
    public void setLoader(ConfigurationLoader<CommentedConfigurationNode> loader) {
        this.loader = loader;
    }


    public BaseConfig() {}

    public void save() {
        try {
            node.setValue(TypeToken.of(BaseConfig.class), this);
            loader.save(node);
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }

    public static <T extends BaseConfig> T load(Path path, T defaultConfig) {
        ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setPath(path)
                .build();
        CommentedConfigurationNode node;

        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            node = loader.createEmptyNode();
        }

        T config = null;
        try {
            config = (T)node.getValue(TypeToken.of(defaultConfig.getClass()));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        if (config == null) {
            config = defaultConfig;
        }

        config.setLoader(loader);
        config.setNode(node);
        config.save();

        return config;
    }
}
