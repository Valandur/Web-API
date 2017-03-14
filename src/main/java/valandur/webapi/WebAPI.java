package valandur.webapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.sun.media.jfxmedia.events.PlayerEvent;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
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
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;
import org.spongepowered.api.event.world.UnloadWorldEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.statistic.achievement.Achievement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.cache.*;
import valandur.webapi.command.*;
import valandur.webapi.handlers.AuthHandler;
import valandur.webapi.handlers.RateLimitHandler;
import valandur.webapi.handlers.WebAPIErrorHandler;
import valandur.webapi.hooks.WebHook;
import valandur.webapi.hooks.WebHooks;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.WebAPICommandSource;
import valandur.webapi.misc.JettyLogger;
import valandur.webapi.servlets.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Plugin(
        id = WebAPI.ID,
        version = WebAPI.VERSION,
        name = WebAPI.NAME,
        url = WebAPI.URL,
        description = WebAPI.DESCRIPTION,
        authors = {
                "Valandur"
        }
)
public class WebAPI {

    public static final String ID = "webapi";
    public static final String NAME = "Web-API";
    public static final String VERSION = "2.0.2-S6.0";
    public static final String DESCRIPTION = "Access Minecraft through a Web API";
    public static final String URL = "https://github.com/Valandur/Web-API";

    private static WebAPI instance;
    public static WebAPI getInstance() {
        return WebAPI.instance;
    }

    public static SpongeExecutorService syncExecutor;

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

    public static int cmdWaitTime;

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

    @Listener
    public void onInitialization(GameInitializationEvent event) {
        logger.info(WebAPI.NAME + " v" + WebAPI.VERSION + " is starting...");

        logger.info("Setting up jetty logger");
        Log.setLog(new JettyLogger());

        // Create permission handler
        authHandler = new AuthHandler();

        loadConfig(null);

        CommandRegistry.init();

        logger.info(WebAPI.NAME + " ready");
    }

    private void loadConfig(Player player) {
        logger.info("Loading configuration...");

        Tuple<ConfigurationLoader, ConfigurationNode> tup = loadWithDefaults("config.conf", "defaults/config.conf");
        ConfigurationNode config = tup.getSecond();

        serverHost = config.getNode("host").getString();
        serverPort = config.getNode("port").getInt();
        cmdWaitTime = config.getNode("cmdWaitTime").getInt();

        authHandler.reloadConfig();

        WebHooks.reloadConfig();

        CacheConfig.init();

        if (player != null) player.sendMessage(Text.builder().color(TextColors.AQUA).append(Text.of("[" + WebAPI.NAME + "] The configuration files have been reloaded!")).toText());
    }

    private void startWebServer(Player player) {
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
            List<Handler> handlers = new LinkedList<>();

            // Asset handlers
            handlers.add(newContext("/", new AssetHandler(loadAssetString("pages/redoc.html"), "text/html; charset=utf-8")));
            String swaggerString = loadAssetString("swagger.yaml").replaceFirst("<host>", serverHost + ":" + serverPort).replaceFirst("<version>", WebAPI.VERSION);
            handlers.add(newContext("/docs", new AssetHandler(swaggerString, "application/x-yaml")));

            // Main servlet context
            ServletContextHandler servletsContext = new ServletContextHandler();
            servletsContext.setContextPath("/api");

            // Use a list to make requests first go through the auth handler and rate-limit handler
            HandlerList list = new HandlerList();
            list.setHandlers(new Handler[]{ authHandler, new RateLimitHandler(), servletsContext });
            handlers.add(list);

            servletsContext.addServlet(InfoServlet.class, "/info");

            servletsContext.addServlet(HistoryServlet.class, "/history/*");
            servletsContext.addServlet(CmdServlet.class, "/cmd/*");
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

        if (player != null) {
            player.sendMessage(Text.builder().color(TextColors.AQUA).append(Text.of("[" + WebAPI.NAME + "] The web server has been restarted!")).toText());
        }
    }
    private void stopWebServer() {
        if (server != null) {
            try {
                server.stop();
                server = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    public Tuple<ConfigurationLoader, ConfigurationNode> loadWithDefaults(String path, String defaultPath) {
        try {
            Path filePath = configPath.resolve(path);
            if (!Files.exists(filePath))
                Sponge.getAssetManager().getAsset(this, defaultPath).get().copyToDirectory(configPath);

            URL defUrl = Sponge.getAssetManager().getAsset(this, defaultPath).get().getUrl();
            ConfigurationLoader<CommentedConfigurationNode> defLoader = HoconConfigurationLoader.builder().setURL(defUrl).build();
            ConfigurationNode defConfig = defLoader.load();

            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(filePath).build();
            ConfigurationNode config = loader.load();

            config.mergeValuesFrom(defConfig);
            loader.save(config);

            return new Tuple<>(loader, config);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void executeCommand(String command, WebAPICommandSource source) {
        CommandManager cmdManager = Sponge.getGame().getCommandManager();
        cmdManager.process(source, command);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        DataCache.updatePlugins();
        DataCache.updateCommands();

        startWebServer(null);
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        stopWebServer();
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        Optional<Player> p = event.getCause().first(Player.class);

        logger.info("Reloading " + WebAPI.NAME + " v" + WebAPI.VERSION + "...");

        stopWebServer();

        loadConfig(p.orElse(null));

        startWebServer(p.orElse(null));

        logger.info("Reloaded " + WebAPI.NAME);
    }

    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
        DataCache.addWorld(event.getTargetWorld());
    }
    @Listener
    public void onWorldUnload(UnloadWorldEvent event) {
        DataCache.removeWorld(event.getTargetWorld().getUniqueId());
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        DataCache.addPlayer(event.getTargetEntity());
    }
    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        DataCache.removePlayer(event.getTargetEntity().getUniqueId());
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        for (Entity entity : event.getEntities()) {
            DataCache.addEntity(entity);
        }
    }
    @Listener
    public void onEntityDespawn(DestructEntityEvent event) {
        DataCache.removeEntity(event.getTargetEntity().getUniqueId());

        Entity source = null;
        Entity ent = event.getTargetEntity();
        Player target = (Player)event.getTargetEntity();

        Optional<EntityDamageSource> dmgSource = event.getCause().first(EntityDamageSource.class);
        if (dmgSource.isPresent()) source = dmgSource.get().getSource();
        String sourceStr = source != null ? JsonConverter.toString(CachedEntity.copyFrom(source)) : "null";

        String message = "{\"killer\":" + sourceStr + ",\"target\":" + JsonConverter.toString(CachedPlayer.copyFrom(target)) + "}";
        WebHooks.notifyHooks(WebHooks.WebHookType.PLAYER_DEATH, null, message);
    }

    @Listener
    public void onMessage(MessageEvent event) {
        Optional<Player> player = event.getCause().first(Player.class);
        if (!player.isPresent()) return;

        CachedChatMessage msg = DataCache.addChatMessage(player.get(), event.getMessage());
        String message = JsonConverter.toString(msg, true);
        WebHooks.notifyHooks(WebHooks.WebHookType.CHAT, msg.sender.uuid, message);
    }

    @Listener
    public void onPlayerAchievement(GrantAchievementEvent.TargetPlayer event) {
        Optional<CachedPlayer> p = DataCache.getPlayer(event.getTargetEntity().getUniqueId(), false);
        if (!p.isPresent()) return;

        Achievement a = event.getAchievement();

        String message = "{\"player\":" + JsonConverter.toString(p.get()) + ",\"achievement\":" + JsonConverter.toString(a, true) + "}";
        WebHooks.notifyHooks(WebHooks.WebHookType.ACHIEVEMENT, null, message);
    }

    @Listener
    public void onCommand(SendCommandEvent event) {
        JsonNode source = null;
        Optional<Object> src = event.getCause().get(NamedCause.SOURCE, Object.class);
        if (src.isPresent()) {
            source = JsonConverter.toJson(src.get());
        }
        DataCache.addCommandCall(event.getCommand(), event.getArguments(), source, event.getResult());
    }
}
