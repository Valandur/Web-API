package valandur.webapi.cache;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;

public class CacheConfig {

    public static final String configFileName = "cache.conf";

    public static int numChatMessages;
    public static int numCommandCalls;

    public static int durationWorld;
    public static int durationPlayer;
    public static int durationEntity;
    public static int durationTileEntity;

    public static void init() {
        WebAPI api = WebAPI.getInstance();

        Tuple<ConfigurationLoader, ConfigurationNode> tup = api.loadWithDefaults(configFileName, "defaults/" + configFileName);
        ConfigurationNode config = tup.getSecond();

        numChatMessages = config.getNode("chat").getInt();
        numCommandCalls = config.getNode("command").getInt();

        ConfigurationNode cacheNode = config.getNode("duration");
        durationWorld = cacheNode.getNode("world").getInt();
        durationPlayer = cacheNode.getNode("player").getInt();
        durationEntity = cacheNode.getNode("entity").getInt();
        durationTileEntity = cacheNode.getNode("tileEntity").getInt();
    }
}
