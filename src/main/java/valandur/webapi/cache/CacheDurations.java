package valandur.webapi.cache;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import valandur.webapi.WebAPI;

import java.nio.file.Files;
import java.nio.file.Path;

public class CacheDurations {

    public static final String configFileName = "cache.conf";

    public static int plugin;
    public static int world;
    public static int player;
    public static int entity;
    public static int tileEntity;

    public static void init() {
        WebAPI api = WebAPI.getInstance();

        try {
            Path configPath = api.getConfigPath().resolve(configFileName);
            if (!Files.exists(configPath))
                Sponge.getAssetManager().getAsset(api, "defaults/" + configFileName).get().copyToDirectory(api.getConfigPath());

            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(configPath).build();
            ConfigurationNode config = loader.load();

            ConfigurationNode cacheNode = config.getNode("cache", "duration");
            plugin = cacheNode.getNode("plugin").getInt();
            world = cacheNode.getNode("world").getInt();
            player = cacheNode.getNode("player").getInt();
            entity = cacheNode.getNode("entity").getInt();
            tileEntity = cacheNode.getNode("tileEntity").getInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
