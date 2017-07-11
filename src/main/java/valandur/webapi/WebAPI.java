package valandur.webapi;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.event.world.*;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.SpongeExecutorService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.api.block.IBlockService;
import valandur.webapi.api.cache.ICacheService;
import valandur.webapi.api.extension.IExtensionService;
import valandur.webapi.api.server.IServerService;
import valandur.webapi.cache.chat.CachedChatMessage;
import valandur.webapi.cache.command.CachedCommandCall;
import valandur.webapi.api.hook.IWebHookService;
import valandur.webapi.api.json.IJsonService;
import valandur.webapi.api.message.IMessageService;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.servlet.IServletService;
import valandur.webapi.block.BlockService;
import valandur.webapi.block.BlockOperation;
import valandur.webapi.block.BlockOperationStatusChangeEvent;
import valandur.webapi.cache.CacheService;
import valandur.webapi.command.CommandRegistry;
import valandur.webapi.command.CommandSource;
import valandur.webapi.extension.ExtensionService;
import valandur.webapi.handler.AssetHandler;
import valandur.webapi.handler.AuthHandler;
import valandur.webapi.handler.ErrorHandler;
import valandur.webapi.handler.RateLimitHandler;
import valandur.webapi.hook.WebHook;
import valandur.webapi.hook.WebHookSerializer;
import valandur.webapi.hook.WebHookService;
import valandur.webapi.json.JsonService;
import valandur.webapi.message.MessageService;
import valandur.webapi.permission.PermissionService;
import valandur.webapi.server.ServerService;
import valandur.webapi.servlet.ApiServlet;
import valandur.webapi.servlet.ServletService;
import valandur.webapi.servlet.block.BlockServlet;
import valandur.webapi.servlet.clazz.ClassServlet;
import valandur.webapi.servlet.cmd.CmdServlet;
import valandur.webapi.servlet.entity.EntityServlet;
import valandur.webapi.servlet.history.HistoryServlet;
import valandur.webapi.servlet.info.InfoServlet;
import valandur.webapi.servlet.map.MapServlet;
import valandur.webapi.servlet.message.MessageServlet;
import valandur.webapi.servlet.player.PlayerServlet;
import valandur.webapi.servlet.plugin.PluginServlet;
import valandur.webapi.servlet.recipe.RecipeServlet;
import valandur.webapi.servlet.registry.RegistryServlet;
import valandur.webapi.servlet.tileentity.TileEntityServlet;
import valandur.webapi.servlet.user.UserServlet;
import valandur.webapi.servlet.world.WorldServlet;
import valandur.webapi.user.UserPermission;
import valandur.webapi.user.UserPermissionConfigSerializer;
import valandur.webapi.user.Users;
import valandur.webapi.util.JettyLogger;
import valandur.webapi.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

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
    public static final String VERSION = "@version@";
    public static final String DESCRIPTION = "Access Minecraft through a Web API";
    public static final String URL = "https://github.com/Valandur/Web-API";

    private static WebAPI instance;
    public static WebAPI getInstance() {
        return WebAPI.instance;
    }

    private static SpongeExecutorService syncExecutor;

    private Reflections reflections;
    public static Reflections getReflections() {
        return WebAPI.getInstance().reflections;
    }

    private boolean devMode = false;
    public static boolean isDevMode() {
        return WebAPI.getInstance().devMode;
    }

    private boolean adminPanelEnabled = true;
    public static boolean isAdminPanelEnabled() {
        return WebAPI.getInstance().adminPanelEnabled;
    }

    @Inject
    private Logger logger;
    public static Logger getLogger() {
        return WebAPI.getInstance().logger;
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;
    public static Path getConfigPath() {
        return WebAPI.getInstance().configPath;
    }

    @Inject
    private PluginContainer container;
    public static PluginContainer getContainer() {
        return WebAPI.getInstance().container;
    }

    private String serverHost;
    private int serverPortHttp;
    private int serverPortHttps;
    private String keyStoreLocation;
    private Server server;

    private AuthHandler authHandler;
    public static AuthHandler getAuthHandler() {
        return WebAPI.getInstance().authHandler;
    }

    // Services
    private BlockService blockService;
    public static BlockService getBlockService() {
        return WebAPI.getInstance().blockService;
    }

    private CacheService cacheService;
    public static CacheService getCacheService() {
        return WebAPI.getInstance().cacheService;
    }

    private ExtensionService extensionService;
    public static ExtensionService getExtensionService() {
        return WebAPI.getInstance().extensionService;
    }

    private JsonService jsonService;
    public static JsonService getJsonService() {
        return WebAPI.getInstance().jsonService;
    }

    private MessageService messageService;
    public static MessageService getMessageService() {
        return WebAPI.getInstance().messageService;
    }

    private PermissionService permissionService;
    public static PermissionService getPermissionService() {
        return WebAPI.getInstance().permissionService;
    }

    private ServerService serverService;
    public static ServerService getServerService() {
        return WebAPI.getInstance().serverService;
    }

    private ServletService servletService;
    public static ServletService getServletService() {
        return WebAPI.getInstance().servletService;
    }

    private WebHookService webHookService;
    public static WebHookService getWebHookService() {
        return WebAPI.getInstance().webHookService;
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

        // Reusable sync executor to run code on main server thread
        syncExecutor = Sponge.getScheduler().createSyncExecutor(this);

        // Register custom serializers
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(WebHook.class), new WebHookSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(UserPermission.class), new UserPermissionConfigSerializer());

        // Setup services
        this.blockService = new BlockService();
        this.cacheService = new CacheService();
        this.extensionService = new ExtensionService();
        this.jsonService = new JsonService();
        this.messageService = new MessageService();
        this.permissionService = new PermissionService();
        this.serverService = new ServerService();
        this.servletService = new ServletService();
        this.webHookService = new WebHookService();


        // Register services
        Sponge.getServiceManager().setProvider(this, IBlockService.class, blockService);
        Sponge.getServiceManager().setProvider(this, ICacheService.class, cacheService);
        Sponge.getServiceManager().setProvider(this, IExtensionService.class, extensionService);
        Sponge.getServiceManager().setProvider(this, IJsonService.class, jsonService);
        Sponge.getServiceManager().setProvider(this, IMessageService.class, messageService);
        Sponge.getServiceManager().setProvider(this, IPermissionService.class, permissionService);
        Sponge.getServiceManager().setProvider(this, IServerService.class, serverService);
        Sponge.getServiceManager().setProvider(this, IServletService.class, servletService);
        Sponge.getServiceManager().setProvider(this, IWebHookService.class, webHookService);
    }
    @Listener
    public void onInitialization(GameInitializationEvent event) {
        logger.info(NAME + " v" + VERSION + " is starting...");

        logger.info("Setting up jetty logger...");
        Log.setLog(new JettyLogger());

        // Create permission handler
        authHandler = new AuthHandler();

        // Main init function, that is also called when reloading the plugin
        init(null);

        Reflections.log = null;
        this.reflections = new Reflections();

        logger.info("Registering servlets...");
        servletService.registerServlet(BlockServlet.class);
        servletService.registerServlet(ClassServlet.class);
        servletService.registerServlet(CmdServlet.class);
        servletService.registerServlet(EntityServlet.class);
        servletService.registerServlet(HistoryServlet.class);
        servletService.registerServlet(InfoServlet.class);
        servletService.registerServlet(MapServlet.class);
        servletService.registerServlet(MessageServlet.class);
        servletService.registerServlet(PlayerServlet.class);
        servletService.registerServlet(PluginServlet.class);
        servletService.registerServlet(RecipeServlet.class);
        servletService.registerServlet(RegistryServlet.class);
        servletService.registerServlet(TileEntityServlet.class);
        servletService.registerServlet(UserServlet.class);
        servletService.registerServlet(WorldServlet.class);

        logger.info(WebAPI.NAME + " ready");
    }
    @Listener(order = Order.POST)
    public void onPostInitialization(GamePostInitializationEvent event) {
        // Load base data
        cacheService.updateWorlds();
        cacheService.updatePlugins();
        cacheService.updateCommands();
    }

    private void init(Player triggeringPlayer) {
        logger.info("Loading configuration...");

        Tuple<ConfigurationLoader, ConfigurationNode> tup = Util.loadWithDefaults("config.conf", "defaults/config.conf");
        ConfigurationNode config = tup.getSecond();

        devMode = config.getNode("devMode").getBoolean();
        serverHost = config.getNode("host").getString();
        serverPortHttp = config.getNode("http").getInt(-1);
        serverPortHttps = config.getNode("https").getInt(-1);
        adminPanelEnabled = config.getNode("adminPanel").getBoolean();
        keyStoreLocation = config.getNode("customKeyStore").getString();
        CmdServlet.CMD_WAIT_TIME = config.getNode("cmdWaitTime").getInt();
        BlockService.MAX_BLOCK_GET_SIZE = config.getNode("maxBlockGetSize").getInt();
        BlockService.MAX_BLOCK_UPDATE_SIZE = config.getNode("maxBlockUpdateSize").getInt();
        BlockOperation.MAX_BLOCKS_PER_SECOND = config.getNode("maxBlocksPerSecond").getInt();

        if (devMode)
            logger.info("WebAPI IS RUNNING IN DEV MODE. USING NON-SHADOWED REFERENCES!");

        authHandler.init();

        extensionService.init();

        cacheService.init();

        webHookService.init();

        jsonService.init();

        serverService.init();

        CommandRegistry.init();

        Users.init();

        if (triggeringPlayer != null) {
            triggeringPlayer.sendMessage(Text.builder().color(TextColors.AQUA)
                    .append(Text.of("[" + WebAPI.NAME + "] " + WebAPI.NAME + " has been reloaded!")).build());
        }
    }
    private void startWebServer(Player player) {
        // Start web server
        logger.info("Starting Web Server...");

        try {
            server = new Server();

            // HTTP config
            HttpConfiguration httpConfig = new HttpConfiguration();
            httpConfig.setOutputBufferSize(32768);

            String tempUri = null;

            // HTTP
            if (serverPortHttp >= 0) {
                ServerConnector httpConn = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
                httpConn.setHost(serverHost);
                httpConn.setPort(serverPortHttp);
                httpConn.setIdleTimeout(30000);
                server.addConnector(httpConn);

                tempUri = "http://" + serverHost + ":" + serverPortHttp;
            }

            // HTTPS
            if (serverPortHttps >= 0) {
                // Update http config
                httpConfig.setSecureScheme("https");
                httpConfig.setSecurePort(serverPortHttps);

                String loc = keyStoreLocation;
                if (loc == null || loc.isEmpty()) {
                    loc = Sponge.getAssetManager().getAsset(WebAPI.getInstance(), "keystore.jks")
                            .map(a -> a.getUrl().toString()).orElse(null);
                }

                // SSL Factory
                SslContextFactory sslFactory = new SslContextFactory();
                sslFactory.setKeyStorePath(loc);
                sslFactory.setKeyStorePassword("mX4z%&uJ2E6VN#5f");
                sslFactory.setKeyManagerPassword("mX4z%&uJ2E6VN#5f");

                // HTTPS config
                HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
                SecureRequestCustomizer src = new SecureRequestCustomizer();
                src.setStsMaxAge(2000);
                src.setStsIncludeSubDomains(true);
                httpsConfig.addCustomizer(src);


                ServerConnector httpsConn = new ServerConnector(server,
                        new SslConnectionFactory(sslFactory, HttpVersion.HTTP_1_1.asString()),
                        new HttpConnectionFactory(httpsConfig)
                );
                httpsConn.setHost(serverHost);
                httpsConn.setPort(serverPortHttps);
                httpsConn.setIdleTimeout(30000);
                server.addConnector(httpsConn);

                tempUri = "https://" + serverHost + ":" + serverPortHttps;
            }

            if (tempUri == null) {
                logger.error("You have disabled both HTTP and HTTPS - The WebAPI will be unreachable!");
            }

            // Add error handler
            server.addBean(new ErrorHandler(server));

            // Collection of all handlers
            List<Handler> handlers = new LinkedList<>();

            final String baseUri = tempUri;

            // Asset handlers
            handlers.add(newContext("/docs", new AssetHandler("pages/redoc.html")));
            handlers.add(newContext("/swagger", new AssetHandler("swagger", (path) -> {
                if (!path.endsWith("/swagger/index.yaml"))
                    return null;
                return (data) -> {
                    String text = new String(data);
                    text = text.replaceFirst("<host>", baseUri);
                    text = text.replaceFirst("<version>", WebAPI.VERSION);
                    return text.getBytes();
                };
            })));

            if (adminPanelEnabled)
                handlers.add(newContext("/admin", new AssetHandler("admin")));

            // Setup all servlets
            servletService.init();

            // Main servlet context
            ServletContextHandler servletsContext = new ServletContextHandler();
            servletsContext.setContextPath("/api");
            servletsContext.addServlet(ApiServlet.class, "/*");

            // Use a list to make requests first go through the auth handler and rate-limit handler
            HandlerList list = new HandlerList();
            list.setHandlers(new Handler[]{ authHandler, new RateLimitHandler(), servletsContext });
            handlers.add(list);

            // Add collection of handlers to server
            ContextHandlerCollection coll = new ContextHandlerCollection();
            coll.setHandlers(handlers.toArray(new Handler[handlers.size()]));
            server.setHandler(coll);

            server.start();

            logger.info("AdminPanel: " + baseUri + "/admin");
            logger.info("API Docs: " + baseUri + "/docs");
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private ContextHandler newContext(String path, Handler handler) {
        ContextHandler context = new ContextHandler();
        context.setContextPath(path);
        context.setHandler(handler);
        return context;
    }

    public static CommandResult executeCommand(String command, CommandSource source) {
        CommandManager cmdManager = Sponge.getGame().getCommandManager();
        return cmdManager.process(source, command);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        startWebServer(null);

        webHookService.notifyHooks(WebHookService.WebHookType.SERVER_START, event);
    }
    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        webHookService.notifyHooks(WebHookService.WebHookType.SERVER_STOP, event);

        stopWebServer();
    }
    @Listener
    public void onReload(GameReloadEvent event) {
        Optional<Player> p = event.getCause().first(Player.class);

        logger.info("Reloading " + WebAPI.NAME + " v" + WebAPI.VERSION + "...");

        cacheService.updatePlugins();
        cacheService.updateCommands();

        stopWebServer();

        init(p.orElse(null));

        startWebServer(p.orElse(null));

        logger.info("Reloaded " + WebAPI.NAME);
    }

    @Listener(order = Order.POST)
    public void onWorldLoad(LoadWorldEvent event) {
        cacheService.updateWorld(event.getTargetWorld());

        webHookService.notifyHooks(WebHookService.WebHookType.WORLD_LOAD, event);
    }
    @Listener(order = Order.POST)
    public void onWorldUnload(UnloadWorldEvent event) {
        cacheService.updateWorld(event.getTargetWorld().getProperties());

        webHookService.notifyHooks(WebHookService.WebHookType.WORLD_UNLOAD, event);
    }
    @Listener(order = Order.POST)
    public void onWorldSave(SaveWorldEvent event) {
        webHookService.notifyHooks(WebHookService.WebHookType.WORLD_SAVE, event);
    }

    @Listener(order = Order.POST)
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        cacheService.updatePlayer(event.getTargetEntity());

        webHookService.notifyHooks(WebHookService.WebHookType.PLAYER_JOIN, event);
    }
    @Listener(order = Order.POST)
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        // Send the message first because the player is removed from cache afterwards
        webHookService.notifyHooks(WebHookService.WebHookType.PLAYER_LEAVE, event);

        cacheService.removePlayer(event.getTargetEntity().getUniqueId());
    }

    @Listener(order = Order.POST)
    public void onUserKick(KickPlayerEvent event) {
        webHookService.notifyHooks(WebHookService.WebHookType.PLAYER_KICK, event);
    }
    @Listener(order = Order.POST)
    public void onUserBan(BanUserEvent event) {
        webHookService.notifyHooks(WebHookService.WebHookType.PLAYER_BAN, event);
    }

    @Listener(order = Order.POST)
    public void onEntitySpawn(SpawnEntityEvent event) {
        for (Entity entity : event.getEntities()) {
            cacheService.updateEntity(entity);
        }
    }
    @Listener(order = Order.POST)
    public void onEntityDespawn(DestructEntityEvent event) {
        cacheService.removeEntity(event.getTargetEntity().getUniqueId());

        Entity ent = event.getTargetEntity();
        if (ent instanceof Player) {
            webHookService.notifyHooks(WebHookService.WebHookType.PLAYER_DEATH, event);
        }
    }

    @Listener(order = Order.POST)
    public void onPlayerChat(MessageChannelEvent.Chat event, @First Player player) {
        CachedChatMessage msg = cacheService.addChatMessage(player, event);
        webHookService.notifyHooks(WebHookService.WebHookType.CHAT, msg);
    }

    @Listener(order = Order.POST)
    public void onInteractBlock(InteractBlockEvent event) {
        webHookService.notifyHooks(WebHookService.WebHookType.INTERACT_BLOCK, event);
    }
    @Listener(order = Order.POST)
    public void onInteractInventory(InteractInventoryEvent.Open event) {
        webHookService.notifyHooks(WebHookService.WebHookType.INVENTORY_OPEN, event);
    }
    @Listener(order = Order.POST)
    public void onInteractInventory(InteractInventoryEvent.Close event) {
        webHookService.notifyHooks(WebHookService.WebHookType.INVENTORY_CLOSE, event);
    }

    @Listener(order = Order.POST)
    public void onPlayerAchievement(GrantAchievementEvent.TargetPlayer event) {
        Player player = event.getTargetEntity();

        // Check if we already have the achievement
        if (player.getAchievementData().achievements().get().stream().anyMatch(a -> a.getId().equals(event.getAchievement().getId())))
            return;

        webHookService.notifyHooks(WebHookService.WebHookType.ACHIEVEMENT, event);
    }

    @Listener(order = Order.POST)
    public void onGenerateChunk(GenerateChunkEvent event) {
        webHookService.notifyHooks(WebHookService.WebHookType.GENERATE_CHUNK, event);
    }

    @Listener(order = Order.POST)
    public void onExplosion(ExplosionEvent event) {
        webHookService.notifyHooks(WebHookService.WebHookType.EXPLOSION, event);
    }

    @Listener(order = Order.POST)
    public void onCommand(SendCommandEvent event) {
        CachedCommandCall call = cacheService.addCommandCall(event);

        webHookService.notifyHooks(WebHookService.WebHookType.COMMAND, call);
    }

    @Listener(order = Order.POST)
    public void onBlockUpdateStatusChange(BlockOperationStatusChangeEvent event) {
        BlockOperation update = event.getBlockOperation();
        switch (update.getStatus()) {
            case DONE:
                logger.info("Block update " + update.getUUID() + " is done, " + update.getBlocksProcessed() + " blocks set");
                break;

            case ERRORED:
                logger.warn("Block update " + update.getUUID() + " failed: " + update.getError());
                break;

            case PAUSED:
                logger.info("Block update " + update.getUUID() + " paused");
                break;

            case RUNNING:
                logger.info("Block update " + update.getUUID() + " started");
                break;
        }

        webHookService.notifyHooks(WebHookService.WebHookType.BLOCK_UPDATE_STATUS, event);
    }

    public static void runOnMain(Runnable runnable) {
        if (Sponge.getServer().isMainThread()) {
            runnable.run();
        } else {
            CompletableFuture future = CompletableFuture.runAsync(runnable, WebAPI.syncExecutor);
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
    public static <T> Optional<T> runOnMain(Supplier<T> supplier) {
        if (Sponge.getServer().isMainThread()) {
            T obj = supplier.get();
            return obj == null ? Optional.empty() : Optional.of(obj);
        } else {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(supplier, WebAPI.syncExecutor);
            try {
                T obj = future.get();
                if (obj == null)
                    return Optional.empty();
                return Optional.of(obj);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
    }
}
