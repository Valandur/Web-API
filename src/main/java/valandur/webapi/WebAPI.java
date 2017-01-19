package valandur.webapi;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
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
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;
import valandur.webapi.cache.CacheConfig;
import valandur.webapi.cache.DataCache;
import valandur.webapi.command.*;
import valandur.webapi.handlers.AuthHandler;
import valandur.webapi.handlers.RateLimitHandler;
import valandur.webapi.handlers.WebAPIErrorHandler;
import valandur.webapi.misc.JsonConverter;
import valandur.webapi.misc.WebAPICommandSource;
import valandur.webapi.misc.JettyLogger;
import valandur.webapi.servlets.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = WebAPI.ID,
        name = WebAPI.NAME,
        url = WebAPI.URL,
        description = WebAPI.DESCRIPTION,
        version = WebAPI.VERSION,
        authors = {
                "Valandur "
        }
)
public class WebAPI {

    public static final String ID = "webapi";
    public static final String NAME = "Web-API";
    public static final String URL = "https://github.com/Valandur/Web-API";
    public static final String DESCRIPTION = "Access Minecraft through a Web API";
    public static final String VERSION = "1.7";

    private static WebAPI instance;
    public static WebAPI getInstance() {
        return WebAPI.instance;
    }

    public SpongeExecutorService syncExecutor;

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
    private RateLimitHandler rateLimitHandler;
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

        this.syncExecutor = Sponge.getScheduler().createSyncExecutor(this);
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

        ConfigurationNode config = loadConfig("config.conf").getNode("server");
        serverHost = config.getNode("host").getString("localhost");
        serverPort = config.getNode("port").getInt(8080);
        JsonConverter.hiddenClasses = config.getNode("hiddenClasses").getList(t -> t.toString().toLowerCase());

        // Load permissions & auth handler
        authHandler = new AuthHandler();

        // Load rate limit handler;
        rateLimitHandler = new RateLimitHandler();


        // Load cache & cache config
        CacheConfig.init();


        // Register commands
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


        // Start web server
        logger.info("Starting Web Server...");

        try {
            server = new Server();

            // HTTP connector
            ServerConnector http = new ServerConnector(server);
            http.setHost(serverHost);
            http.setPort(serverPort);
            http.setIdleTimeout(30000);
            server.addConnector(http);

            // Add error handler
            server.addBean(new WebAPIErrorHandler());

            // Collection of all handlers
            List<Handler> handlers = new ArrayList<Handler>();

            // Asset handlers
            handlers.add(newContext("/", new AssetHandler(loadAssetString("pages/redoc.html"), "text/html; charset=utf-8")));
            String swaggerString = loadAssetString("swagger.yaml").replaceFirst("<host>", serverHost + ":" + serverPort).replaceFirst("<version>", WebAPI.VERSION);
            handlers.add(newContext("/docs", new AssetHandler(swaggerString, "application/x-yaml")));

            // Main servlet context
            ServletContextHandler servletsContext = new ServletContextHandler();
            servletsContext.setContextPath("/api");

            // Use a list to make request first go through the auth handler and rate-limit handler
            HandlerList list = new HandlerList();
            list.setHandlers(new Handler[]{ authHandler, rateLimitHandler, servletsContext });
            handlers.add(list);

            servletsContext.addServlet(InfoServlet.class, "/info");
            servletsContext.addServlet(ChatServlet.class, "/chat");
            servletsContext.addServlet(CmdServlet.class, "/cmd");

            servletsContext.addServlet(WorldServlet.class, "/world/*");
            servletsContext.addServlet(PlayerServlet.class, "/player/*");
            servletsContext.addServlet(PluginServlet.class, "/plugin/*");
            servletsContext.addServlet(RecipeServlet.class, "/recipe/*");
            servletsContext.addServlet(EntityServlet.class, "/entity/*");
            servletsContext.addServlet(TileEntityServlet.class, "/tile-entity/*");

            servletsContext.addServlet(ClassServlet.class, "/class/*");

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
        DataCache.updatePlugins();
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
    public void onWorldLoad(LoadWorldEvent e) {
        DataCache.addWorld(e.getTargetWorld());
    }
    @Listener
    public void onWorldUnload(UnloadWorldEvent e) {
        DataCache.removeWorld(e.getTargetWorld().getUniqueId());
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join e) {
        DataCache.addPlayer(e.getTargetEntity());
    }
    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect e) {
        DataCache.removePlayer(e.getTargetEntity().getUniqueId());
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent e) {
        for (Entity entity : e.getEntities()) {
            DataCache.addEntity(entity);
        }
    }
    @Listener
    public void onEntityDespawn(DestructEntityEvent e) {
        DataCache.removeEntity(e.getTargetEntity().getUniqueId());
    }

    public static WebAPICommandSource executeCommand(String command) {
        WebAPICommandSource src = new WebAPICommandSource();
        CommandManager cmdManager = Sponge.getGame().getCommandManager();
        cmdManager.process(src, command);
        return src;
    }

    @Listener
    public void onMessage(MessageChannelEvent.Chat event) {
        Optional<Player> player = event.getCause().first(Player.class);
        if (!player.isPresent()) return;

        DataCache.addChatMessage(player.get(), event.getRawMessage());
    }
}
