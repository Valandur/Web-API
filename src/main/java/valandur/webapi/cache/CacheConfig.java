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

        ConfigurationNode amountNode = config.getNode("amount");
        numChatMessages = amountNode.getNode("chat").getInt();
        numCommandCalls = amountNode.getNode("command").getInt();

        ConfigurationNode durationNode = config.getNode("duration");
        durationWorld = durationNode.getNode("world").getInt();
        durationPlayer = durationNode.getNode("player").getInt();
        durationEntity = durationNode.getNode("entity").getInt();
        durationTileEntity = durationNode.getNode("tileEntity").getInt();
    }
}
