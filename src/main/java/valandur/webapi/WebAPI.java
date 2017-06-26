package valandur.webapi;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
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
import org.spongepowered.api.asset.Asset;
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
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
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
import valandur.webapi.api.service.IServletService;
import valandur.webapi.api.servlet.IServlet;
import valandur.webapi.block.BlockUpdate;
import valandur.webapi.block.BlockUpdateStatusChangeEvent;
import valandur.webapi.block.Blocks;
import valandur.webapi.cache.CacheConfig;
import valandur.webapi.cache.DataCache;
import valandur.webapi.cache.chat.CachedChatMessage;
import valandur.webapi.cache.command.CachedCommandCall;
import valandur.webapi.command.CommandRegistry;
import valandur.webapi.command.CommandSource;
import valandur.webapi.handler.AssetHandler;
import valandur.webapi.handler.AuthHandler;
import valandur.webapi.handler.ErrorHandler;
import valandur.webapi.handler.RateLimitHandler;
import valandur.webapi.hook.WebHook;
import valandur.webapi.hook.WebHookSerializer;
import valandur.webapi.hook.WebHooks;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.Extensions;
import valandur.webapi.misc.JettyLogger;
import valandur.webapi.misc.Util;
import valandur.webapi.servlet.ApiServlet;
import valandur.webapi.servlet.Servlets;
import valandur.webapi.servlet.block.BlockServlet;
import valandur.webapi.servlet.clazz.ClassServlet;
import valandur.webapi.servlet.cmd.CmdServlet;
import valandur.webapi.servlet.entity.EntityServlet;
import valandur.webapi.servlet.history.HistoryServlet;
import valandur.webapi.servlet.info.InfoServlet;
import valandur.webapi.servlet.message.MessageServlet;
import valandur.webapi.servlet.player.PlayerServlet;
import valandur.webapi.servlet.plugin.PluginServlet;
import valandur.webapi.servlet.recipe.RecipeServlet;
import valandur.webapi.servlet.registry.RegistryServlet;
import valandur.webapi.servlet.tileentity.TileEntityServlet;
import valandur.webapi.servlet.user.UserServlet;
import valandur.webapi.servlet.world.WorldServlet;
import valandur.webapi.server.ServerProperties;
import valandur.webapi.user.UserPermission;
import valandur.webapi.user.UserPermissionConfigSerializer;
import valandur.webapi.user.Users;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
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
public class WebAPI implements IServletService {

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
    public Reflections getReflections() { return this.reflections; }

    private boolean devMode = false;
    public boolean isDevMode() {
        return devMode;
    }

    private boolean adminPanelEnabled = true;
    public boolean isAdminPanelEnabled() {
        return adminPanelEnabled;
    }

    @Inject
    private Logger logger;
    public Logger getLogger() {
        return this.logger;
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configPath;

    @Inject
    private PluginContainer container;
    public PluginContainer getContainer() { return this.container; }

    private String serverHost;
    private int serverPortHttp;
    private int serverPortHttps;
    private String keyStoreLocation;
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

        // Reusable sync executor to run code on main server thread
        syncExecutor = Sponge.getScheduler().createSyncExecutor(this);

        // Register custom serializers
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(WebHook.class), new WebHookSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(UserPermission.class), new UserPermissionConfigSerializer());

        // Register API services
        Sponge.getServiceManager().setProvider(this, IServletService.class, this);
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

        logger.info("Loading base data...");
        DataCache.updateWorlds();
        DataCache.updatePlugins();
        DataCache.updateCommands();

        logger.info("Registering servlets...");
        Servlets.registerServlet(BlockServlet.class);
        Servlets.registerServlet(ClassServlet.class);
        Servlets.registerServlet(CmdServlet.class);
        Servlets.registerServlet(EntityServlet.class);
        Servlets.registerServlet(HistoryServlet.class);
        Servlets.registerServlet(InfoServlet.class);
        Servlets.registerServlet(MessageServlet.class);
        Servlets.registerServlet(PlayerServlet.class);
        Servlets.registerServlet(PluginServlet.class);
        Servlets.registerServlet(RecipeServlet.class);
        Servlets.registerServlet(RegistryServlet.class);
        Servlets.registerServlet(TileEntityServlet.class);
        Servlets.registerServlet(UserServlet.class);
        Servlets.registerServlet(WorldServlet.class);

        logger.info(WebAPI.NAME + " ready");
    }

    private void init(Player triggeringPlayer) {
        logger.info("Loading configuration...");

        Tuple<ConfigurationLoader, ConfigurationNode> tup = loadWithDefaults("config.conf", "defaults/config.conf");
        ConfigurationNode config = tup.getSecond();

        devMode = config.getNode("devMode").getBoolean();
        serverHost = config.getNode("host").getString();
        serverPortHttp = config.getNode("http").getInt(-1);
        serverPortHttps = config.getNode("https").getInt(-1);
        adminPanelEnabled = config.getNode("adminPanel").getBoolean();
        keyStoreLocation = config.getNode("customKeyStore").getString();
        CmdServlet.CMD_WAIT_TIME = config.getNode("cmdWaitTime").getInt();
        Blocks.MAX_BLOCK_GET_SIZE = config.getNode("maxBlockGetSize").getInt();
        Blocks.MAX_BLOCK_UPDATE_SIZE = config.getNode("maxBlockUpdateSize").getInt();
        BlockUpdate.MAX_BLOCKS_PER_SECOND = config.getNode("maxBlocksPerSecond").getInt();

        if (devMode)
            logger.info("WebAPI IS RUNNING IN DEV MODE. USING NON-SHADOWED REFERENCES!");

        authHandler.init();

        Extensions.init();

        WebHooks.init();

        JsonConverter.init();

        CacheConfig.init();

        CommandRegistry.init();

        Users.init();

        ServerProperties.init();

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
            Servlets.init();

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

    public Tuple<ConfigurationLoader, ConfigurationNode> loadWithDefaults(String path, String defaultPath) {
        try {
            Path filePath = configPath.resolve(path);
            Asset asset = Sponge.getAssetManager().getAsset(this, defaultPath).get();

            if (!Files.exists(filePath))
                asset.copyToDirectory(configPath);

            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setPath(filePath).build();
            CommentedConfigurationNode config = loader.load();

            ConfigurationLoader<CommentedConfigurationNode> defLoader = HoconConfigurationLoader.builder().setURL(asset.getUrl()).build();
            CommentedConfigurationNode defConfig = defLoader.load();

            int version = config.getNode("version").getInt(0);
            int defVersion = defConfig.getNode("version").getInt(0);
            boolean newVersion = defVersion != version;

            Util.mergeConfigs(config, defConfig, newVersion);
            loader.save(config);

            if (newVersion) {
                logger.info("New configuration version '" + defVersion + "' for " + path);
                config.getNode("version").setValue(defVersion);
                loader.save(config);
            }

            return new Tuple<>(loader, config);

        } catch (IOException | NoSuchElementException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CommandResult executeCommand(String command, CommandSource source) {
        CommandManager cmdManager = Sponge.getGame().getCommandManager();
        return cmdManager.process(source, command);
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        startWebServer(null);

        WebHooks.notifyHooks(WebHooks.WebHookType.SERVER_START, event);
    }
    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        WebHooks.notifyHooks(WebHooks.WebHookType.SERVER_STOP, event);

        stopWebServer();
    }
    @Listener
    public void onReload(GameReloadEvent event) {
        Optional<Player> p = event.getCause().first(Player.class);

        logger.info("Reloading " + WebAPI.NAME + " v" + WebAPI.VERSION + "...");

        DataCache.updatePlugins();
        DataCache.updateCommands();

        stopWebServer();

        init(p.orElse(null));

        startWebServer(p.orElse(null));

        logger.info("Reloaded " + WebAPI.NAME);
    }

    @Listener(order = Order.POST)
    public void onWorldLoad(LoadWorldEvent event) {
        DataCache.updateWorld(event.getTargetWorld());

        WebHooks.notifyHooks(WebHooks.WebHookType.WORLD_LOAD, event);
    }
    @Listener(order = Order.POST)
    public void onWorldUnload(UnloadWorldEvent event) {
        DataCache.updateWorld(event.getTargetWorld().getProperties());

        WebHooks.notifyHooks(WebHooks.WebHookType.WORLD_UNLOAD, event);
    }
    @Listener(order = Order.POST)
    public void onWorldSave(SaveWorldEvent event) {
        WebHooks.notifyHooks(WebHooks.WebHookType.WORLD_SAVE, event);
    }

    @Listener(order = Order.POST)
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        DataCache.updatePlayer(event.getTargetEntity());

        WebHooks.notifyHooks(WebHooks.WebHookType.PLAYER_JOIN, event);
    }
    @Listener(order = Order.POST)
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        // Send the message first because the player is removed from cache afterwards
        WebHooks.notifyHooks(WebHooks.WebHookType.PLAYER_LEAVE, event);

        DataCache.removePlayer(event.getTargetEntity().getUniqueId());
    }

    @Listener(order = Order.POST)
    public void onUserKick(KickPlayerEvent event) {
        WebHooks.notifyHooks(WebHooks.WebHookType.PLAYER_KICK, event);
    }
    @Listener(order = Order.POST)
    public void onUserBan(BanUserEvent event) {
        WebHooks.notifyHooks(WebHooks.WebHookType.PLAYER_BAN, event);
    }

    @Listener(order = Order.POST)
    public void onEntitySpawn(SpawnEntityEvent event) {
        for (Entity entity : event.getEntities()) {
            DataCache.updateEntity(entity);
        }
    }
    @Listener(order = Order.POST)
    public void onEntityDespawn(DestructEntityEvent event) {
        DataCache.removeEntity(event.getTargetEntity().getUniqueId());

        Entity ent = event.getTargetEntity();
        if (ent instanceof Player) {
            WebHooks.notifyHooks(WebHooks.WebHookType.PLAYER_DEATH, event);
        }
    }

    @Listener(order = Order.POST)
    public void onPlayerChat(MessageChannelEvent.Chat event, @First Player player) {
        CachedChatMessage msg = DataCache.addChatMessage(player, event);
        WebHooks.notifyHooks(WebHooks.WebHookType.CHAT, msg);
    }

    @Listener(order = Order.POST)
    public void onInteractBlock(InteractBlockEvent event) {
        WebHooks.notifyHooks(WebHooks.WebHookType.INTERACT_BLOCK, event);
    }
    @Listener(order = Order.POST)
    public void onInteractInventory(InteractInventoryEvent.Open event) {
        WebHooks.notifyHooks(WebHooks.WebHookType.INVENTORY_OPEN, event);
    }
    @Listener(order = Order.POST)
    public void onInteractInventory(InteractInventoryEvent.Close event) {
        WebHooks.notifyHooks(WebHooks.WebHookType.INVENTORY_CLOSE, event);
    }

    @Listener(order = Order.POST)
    public void onPlayerAchievement(GrantAchievementEvent.TargetPlayer event) {
        Player player = event.getTargetEntity();

        // Check if we already have the achievement
        if (player.getAchievementData().achievements().get().stream().anyMatch(a -> a.getId().equals(event.getAchievement().getId())))
            return;

        WebHooks.notifyHooks(WebHooks.WebHookType.ACHIEVEMENT, event);
    }

    @Listener(order = Order.POST)
    public void onGenerateChunk(GenerateChunkEvent event) {
        WebHooks.notifyHooks(WebHooks.WebHookType.GENERATE_CHUNK, event);
    }

    @Listener(order = Order.POST)
    public void onExplosion(ExplosionEvent event) {
        WebHooks.notifyHooks(WebHooks.WebHookType.EXPLOSION, event);
    }

    @Listener(order = Order.POST)
    public void onCommand(SendCommandEvent event) {
        CachedCommandCall call = DataCache.addCommandCall(event);

        WebHooks.notifyHooks(WebHooks.WebHookType.COMMAND, call);
    }

    @Listener(order = Order.POST)
    public void onBlockUpdateStatusChange(BlockUpdateStatusChangeEvent event) {
        BlockUpdate update = event.getBlockUpdate();
        switch (update.getStatus()) {
            case DONE:
                logger.info("Block update " + update.getUUID() + " is done, " + update.getBlocksSet() + " blocks set");
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

        WebHooks.notifyHooks(WebHooks.WebHookType.BLOCK_UPDATE_STATUS, event);
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

    @Override
    public void registerServlet(Class<? extends IServlet> servlet) {
        Servlets.registerServlet(servlet);
    }
}
