package valandur.webapi.cache;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;

import java.nio.file.Files;
import java.nio.file.Path;

public class CacheConfig {

    public static final String configFileName = "cache.conf";

    public static int plugin;
    public static int world;
    public static int player;
    public static int entity;
    public static int tileEntity;
    public static int chatMessages;
    public static int commandCalls;

    public static void init() {
        WebAPI api = WebAPI.getInstance();

        Tuple<ConfigurationLoader, ConfigurationNode> tup = api.loadWithDefaults(configFileName, "defaults/" + configFileName);
        ConfigurationNode config = tup.getSecond();

        chatMessages = config.getNode("chat").getInt();
        commandCalls = config.getNode("command").getInt();

        ConfigurationNode cacheNode = config.getNode("duration");
        plugin = cacheNode.getNode("plugin").getInt();
        world = cacheNode.getNode("world").getInt();
        player = cacheNode.getNode("player").getInt();
        entity = cacheNode.getNode("entity").getInt();
        tileEntity = cacheNode.getNode("tileEntity").getInt();
    }
}
