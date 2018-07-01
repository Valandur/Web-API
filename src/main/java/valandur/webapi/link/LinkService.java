package valandur.webapi.link;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppingEvent;
import valandur.webapi.WebAPI;
import valandur.webapi.config.BaseConfig;
import valandur.webapi.config.LinkConfig;
import valandur.webapi.link.rabbitmq.RabbitMQClient;
import valandur.webapi.link.redis.RedisClient;
import valandur.webapi.link.ws.WSClient;

import java.nio.file.Path;

/**
 * This service provides inter-process/inter-plugin links
 */
public class LinkService {

    private final static String configFileName = "link.conf";

    private LinkClient linkClient;
    public LinkClient getLinkClient() {
        return linkClient;
    }


    public void init() {
        Path configPath = WebAPI.getConfigPath().resolve(configFileName).normalize();
        LinkConfig config = BaseConfig.load(configPath, new LinkConfig());

        // Close any existing connections
        if (linkClient != null) {
            linkClient.disconnect();
        }
        // Create a new client
        linkClient = createLinkClient(config);
        // Connect if we have a client
        if (linkClient != null) {
            linkClient.connect(config.url);
        }
    }

    private LinkClient createLinkClient(LinkConfig config) {
        switch (config.type) {
            case WebSocket:
                return new WSClient();
            case Redis:
                return new RedisClient();
            case RabbitMQ:
                return new RabbitMQClient();
        }
        return null;
    }

    @Listener
    public void onServerStop(GameStoppingEvent event) {
        linkClient.disconnect();
    }
}
