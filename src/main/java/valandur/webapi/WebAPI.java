package valandur.webapi;

import com.google.inject.Inject;
import org.apache.commons.lang3.tuple.Triple;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import valandur.webapi.handlers.Handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Plugin(
        id = "webapi",
        name = "Web-API",
        url = "https://github.com/Valandur",
        description = "Access Minecraft through a Web API",
        version = "1.0-SNAPSHOT",
        authors = {
                "Valandur"
        }
)
public class WebAPI {

    private static WebAPI instance;
    public static WebAPI getInstance() {
        return WebAPI.instance;
    }

    private static List<Triple<Date, Player, Text>> chatMessages = new ArrayList<>();
    public static List<Triple<Date, Player, Text>> getChatMessages() {
        return WebAPI.chatMessages;
    }

    @Inject
    private Logger logger;
    public static Logger getLogger() {
        return WebAPI.instance.logger;
    }

    private Server server;

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        WebAPI.instance = this;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Starting Web Server...");

        try {
            server = new Server();

            // HTTP connector
            ServerConnector http = new ServerConnector(server);
            http.setHost("localhost");
            http.setPort(8080);
            http.setIdleTimeout(30000);

            server.addConnector(http);
            server.setHandler(Handlers.get());
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Web server running on " + server.getURI());
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Listener
    public void onMessage(MessageChannelEvent.Chat event) {
        Optional<Player> player = event.getCause().first(Player.class);
        if (!player.isPresent()) return;

        WebAPI.chatMessages.add(Triple.of(new Date(), player.get(), event.getRawMessage()));
    }
}
