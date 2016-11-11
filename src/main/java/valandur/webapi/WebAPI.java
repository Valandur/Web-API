package valandur.webapi;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.apache.commons.lang3.tuple.Triple;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.log.Log;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import valandur.webapi.command.*;
import valandur.webapi.handlers.AuthHandler;
import valandur.webapi.misc.JettyLogger;
import valandur.webapi.servlets.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = "webapi",
        name = "Web-API",
        url = "https://github.com/Valandur/Web-API",
        description = "Access Minecraft through a Web API",
        version = "1.1",
        authors = {
                "Valandur"
        }
)
public class WebAPI {

    private static WebAPI instance;
    public static WebAPI getInstance() {
        return WebAPI.instance;
    }

    private List<Triple<Date, Player, Text>> chatMessages = new ArrayList<>();
    public List<Triple<Date, Player, Text>> getChatMessages() {
        return chatMessages;
    }

    @Inject
    private Logger logger;
    public Logger getLogger() {
        return this.logger;
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;
    public Path getConfigPath() {
        return configPath;
    }

    private String serverHost;
    private int serverPort;
    private Server server;

    private AuthHandler authHandler;
    public AuthHandler getAuthHandler() {
        return authHandler;
    }

    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        WebAPI.instance = this;

        // Create our config directory if it doesn't exist
        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    public ConfigurationNode loadConfig(String configName) {
        try {
            Path filePath = configPath.resolve(configName);
            if (!Files.exists(filePath))
                Sponge.getAssetManager().getAsset(this, "defaults/" + configName).get().copyToDirectory(configPath);

            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(filePath).build();
            ConfigurationNode config = loader.load();
            loader.save(config);

            return config;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        logger.info("Setting up jetty logger");
        Log.setLog(new JettyLogger());


        logger.info("Loading configuration...");

        // Load main config file
        ConfigurationNode config = loadConfig("config.conf");
        serverHost = config.getNode("server", "host").getString("localhost");
        serverPort = config.getNode("server", "port").getInt(8080);

        // Load permissions
        authHandler = new AuthHandler();


        logger.info("Registering commands...");
        CommandSpec specWhitelistAdd = CommandSpec.builder()
                .description(Text.of("Add an IP to the whitelist"))
                .permission("webapi.command.whitelist.add")
                .arguments(new CmdIpElement(Text.of("ip")))
                .executor(new CmdAuthListAdd(true))
                .build();
        CommandSpec specWhitelistRemove = CommandSpec.builder()
                .description(Text.of("Remove an IP from the whitelist"))
                .permission("webapi.command.whitelist.remove")
                .arguments(new CmdIpElement(Text.of("ip")))
                .executor(new CmdAuthListRemove(true))
                .build();
        CommandSpec specWhitelistEnable = CommandSpec.builder()
                .description(Text.of("Enable the whitelist"))
                .permission("webapi.command.whitelist.enable")
                .executor(new CmdAuthListEnable(true))
                .build();
        CommandSpec specWhitelistDisable = CommandSpec.builder()
                .description(Text.of("Disable the whitelist"))
                .permission("webapi.command.whitelist.disable")
                .executor(new CmdAuthListDisable(true))
                .build();
        CommandSpec specWhitelist = CommandSpec.builder()
                .description(Text.of("Manage the whitelist"))
                .permission("webapi.command.whitelist")
                .child(specWhitelistAdd, "add")
                .child(specWhitelistRemove, "remove")
                .child(specWhitelistEnable, "enable")
                .child(specWhitelistDisable, "disable")
                .build();

        CommandSpec specBlacklistAdd = CommandSpec.builder()
                .description(Text.of("Add an IP to the blacklist"))
                .permission("webapi.command.blacklist.add")
                .arguments(GenericArguments.string(Text.of("ip")))
                .executor(new CmdAuthListAdd(false))
                .build();
        CommandSpec specBlaclistRemove = CommandSpec.builder()
                .description(Text.of("Remove an IP from the blacklist"))
                .permission("webapi.command.blacklist.remove")
                .arguments(GenericArguments.string(Text.of("ip")))
                .executor(new CmdAuthListRemove(false))
                .build();
        CommandSpec specBlacklistEnable = CommandSpec.builder()
                .description(Text.of("Enable the blacklist"))
                .permission("webapi.command.blacklist.enable")
                .executor(new CmdAuthListEnable(false))
                .build();
        CommandSpec specBlacklistDisable = CommandSpec.builder()
                .description(Text.of("Disable the blacklist"))
                .permission("webapi.command.blacklist.disable")
                .executor(new CmdAuthListDisable(false))
                .build();
        CommandSpec specBlacklist = CommandSpec.builder()
                .description(Text.of("Manage the blacklist"))
                .permission("webapi.command.blacklist")
                .child(specBlacklistAdd, "add")
                .child(specBlaclistRemove, "remove")
                .child(specBlacklistEnable, "enable")
                .child(specBlacklistDisable, "disable")
                .build();

        CommandSpec spec = CommandSpec.builder()
                .description(Text.of("Manage Web-API settings"))
                .permission("webapi.command")
                .child(specWhitelist, "whitelist")
                .child(specBlacklist, "blacklist")
                .build();
        Sponge.getCommandManager().register(this, spec, "webapi");


        logger.info("Starting Web Server...");

        try {
            server = new Server();

            // HTTP connector
            ServerConnector http = new ServerConnector(server);
            http.setHost(serverHost);
            http.setPort(serverPort);
            http.setIdleTimeout(30000);
            server.addConnector(http);

            // Collection of all handlers
            List<Handler> handlers = new ArrayList<Handler>();

            // Asset handlers
            handlers.add(newContext("/", new AssetHandler(loadAssetString("pages/redoc.html"), "text/html; charset=utf-8")));
            handlers.add(newContext("/docs", new AssetHandler(loadAssetString("swagger.yaml").replaceFirst("<host>", serverHost + ":" + serverPort), "application/x-yaml")));

            // Main servlet context
            ServletContextHandler servletsContext = new ServletContextHandler();
            servletsContext.setContextPath("/api");

            HandlerList list = new HandlerList();
            list.setHandlers(new Handler[]{ authHandler, servletsContext });
            handlers.add(list);

            servletsContext.addServlet(InfoServlet.class, "/info");
            servletsContext.addServlet(ChatServlet.class, "/chat");
            servletsContext.addServlet(CmdServlet.class, "/cmd");

            servletsContext.addServlet(WorldServlet.class, "/worlds/*");
            servletsContext.addServlet(PlayerServlet.class, "/players/*");
            servletsContext.addServlet(PluginServlet.class, "/plugins/*");

            // Add collection of handlers to server
            ContextHandlerCollection coll = new ContextHandlerCollection();
            coll.setHandlers(handlers.toArray(new Handler[handlers.size()]));
            server.setHandler(coll);

            server.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("Web server running on " + server.getURI());
    }

    private String loadAssetString(String assetPath) throws IOException {
        String res = "";
        Optional<Asset> asset = Sponge.getAssetManager().getAsset(this, assetPath);
        if (asset.isPresent()) {
            res = asset.get().readString();
        }
        return res;
    }

    private ContextHandler newContext(String path, Handler handler) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(path);
        context.setHandler(handler);
        return context;
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {

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

        chatMessages.add(Triple.of(new Date(), player.get(), event.getRawMessage()));
    }
}
