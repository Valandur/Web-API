package valandur.webapi.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.io.IOException;

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


    BaseConfig() {}

    public void save() {
        try {
            node.setValue(TypeToken.of(BaseConfig.class), this);
            loader.save(node);
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }
}
