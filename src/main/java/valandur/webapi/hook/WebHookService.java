package valandur.webapi.hook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.achievement.GrantAchievementEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.ExpireEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.event.world.*;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.api.hook.BaseWebHookFilter;
import valandur.webapi.api.hook.IWebHook;
import valandur.webapi.api.hook.IWebHookService;
import valandur.webapi.api.hook.WebHookHeader;
import valandur.webapi.block.BlockOperationStatusChangeEvent;
import valandur.webapi.config.HookConfig;
import valandur.webapi.hook.filter.BlockTypeFilter;
import valandur.webapi.hook.filter.ItemTypeFilter;
import valandur.webapi.hook.filter.PlayerFilter;
import valandur.webapi.util.Constants;
import valandur.webapi.util.Timings;
import valandur.webapi.util.Util;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class WebHookService implements IWebHookService {

    private static final String configFileName = "hooks.conf";
    private static String userAgent = Constants.NAME + "/" + Constants.VERSION;

    private Map<String, CommandWebHook> commandHooks = new HashMap<>();
    private Map<WebHookType, List<WebHook>> eventHooks = new HashMap<>();
    private Map<Class<? extends Event>, Tuple<List<WebHook>, EventListener>> customHooks = new HashMap<>();
    private Map<String, Class<? extends BaseWebHookFilter>> filters = new HashMap<>();

    public Map<String, CommandWebHook> getCommandHooks() {
        return commandHooks;
    }


    public void init() {
        Logger logger = WebAPI.getLogger();

        logger.info("Initializing web hooks...");

        // Remove existing listeners to prevent multiple subscriptions on config reload
        for (Tuple<List<WebHook>, EventListener> entry : customHooks.values()) {
            Sponge.getEventManager().unregisterListeners(entry.getSecond());
        }

        // Save some basic data
        Platform platform = Sponge.getPlatform();
        String mc = platform.getContainer(Platform.Component.GAME).getVersion().orElse("?");
        String sponge = platform.getContainer(Platform.Component.IMPLEMENTATION).getVersion().orElse("?");
        userAgent = Constants.NAME + "/" + Constants.VERSION +
                " Sponge/" + sponge +
                " Minecraft/" + mc +
                " Java/" + System.getProperty("java.version");

        // Load filters
        logger.info("Loading filters...");

        filters.clear();

        // Add some default filters
        filters.put(BlockTypeFilter.name, BlockTypeFilter.class);
        filters.put(PlayerFilter.name, PlayerFilter.class);
        filters.put(ItemTypeFilter.name, ItemTypeFilter.class);

        logger.info("Done loading filters");

        // Load config
        HookConfig config = Util.loadConfig(configFileName, new HookConfig());

        // Clear hooks
        eventHooks.clear();
        customHooks.clear();
        commandHooks.clear();

        // Add command hooks
        for (Map.Entry<String, CommandWebHook> entry : config.command.entrySet()) {
            if (!entry.getValue().isEnabled())
                continue;
            commandHooks.put(entry.getKey(), entry.getValue());
        }

        // Add event hooks
        for (Map.Entry<WebHookType, List<WebHook>> entry : config.events.asMap().entrySet()) {
            eventHooks.put(
                    entry.getKey(),
                    entry.getValue().stream().filter(WebHook::isEnabled).collect(Collectors.toList())
            );
        }

        // Add custom event hooks
        for (Map.Entry<String, List<WebHook>> entry : config.custom.entrySet()) {
            String className = entry.getKey();

            try {
                Class c = Class.forName(className);
                if (!Event.class.isAssignableFrom(c))
                    throw new InvalidClassException("Class " + c.toString() + " must be a subclass of " +
                            Event.class.toString() + " so that it can be used as a custom web hook");
                Class<? extends Event> clazz = (Class<? extends Event>) c;

                WebHookEventListener listener = new WebHookEventListener(clazz);
                List<WebHook> hooks = entry.getValue().stream().filter(WebHook::isEnabled).collect(Collectors.toList());

                Sponge.getEventManager().registerListener(WebAPI.getInstance(), clazz, listener);
                customHooks.put(clazz, new Tuple<>(hooks, listener));
            } catch (ClassNotFoundException e) {
                logger.error("Could not find class for custom web hook: " + className);
            } catch (InvalidClassException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public Optional<Class<? extends BaseWebHookFilter>> getFilter(String name) {
        return filters.containsKey(name) ? Optional.of(filters.get(name)) : Optional.empty();
    }

    @Override
    public void notifyHooks(WebHookType type, Object data) {
        Timings.WEBHOOK_NOTIFY.startTimingIfSync();

        List<WebHook> notifyHooks = new ArrayList<>(eventHooks.get(type));
        notifyHooks.addAll(eventHooks.get(WebHookType.ALL));
        for (WebHook hook : notifyHooks) {
            notifyHook(hook, type, null, data);
        }

        Timings.WEBHOOK_NOTIFY.stopTimingIfSync();
    }
    public void notifyHook(CommandWebHook cmdHook, String source, Map<String, Object> data) {
        Timings.WEBHOOK_NOTIFY.startTimingIfSync();

        for (WebHook hook : cmdHook.getHooks()) {
            notifyHook(hook, WebHookType.CUSTOM_COMMAND, source, data);
        }

        Timings.WEBHOOK_NOTIFY.stopTimingIfSync();
    }

    @Override
    public void notifyHooks(Class<? extends Event> clazz, Object data) {
        Timings.WEBHOOK_NOTIFY.startTimingIfSync();

        List<WebHook> notifyHooks = new ArrayList<>(customHooks.get(clazz).getFirst());
        for (WebHook hook : notifyHooks) {
            notifyHook(hook, WebHookType.CUSTOM_EVENT, null, data);
        }

        Timings.WEBHOOK_NOTIFY.stopTimingIfSync();
    }

    private void notifyHook(WebHook hook, WebHookType eventType, String source, Object data) {
        // First check the filter before we do any processing
        if (hook.getFilter() != null && !hook.getFilter().process(data)) {
            return;
        }

        final String address = hook.getAddress();

        String stringData = "";
        try {
            ObjectMapper om = WebAPI.getSerializeService().getDefaultObjectMapper(
                    hook.getDataType() == IWebHook.WebHookDataType.XML,
                    hook.includeDetails(),
                    hook.getPermissions()
            );
            stringData = om.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (data != null) {
            try {
                stringData = hook.isForm() ? "body=" + URLEncoder.encode(stringData, "UTF-8") : stringData;
            } catch (Exception e) {
                e.printStackTrace();
                if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
            }
        }
        final String finalData = stringData;

        final Logger logger = WebAPI.getLogger();
        CompletableFuture.runAsync(() -> {
            HttpURLConnection connection = null;
            try {
                //Create connection
                URL url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(hook.getMethod());
                for (WebHookHeader header : hook.getHeaders()) {
                    connection.setRequestProperty(header.getName(), header.getValue());
                }
                connection.setRequestProperty("User-Agent", userAgent);
                connection.setRequestProperty("X-WebAPI-Version", Constants.VERSION);
                connection.setRequestProperty("X-WebAPI-Event", eventType.toString());
                if (source != null) connection.setRequestProperty("X-WebAPI-Source", source);
                connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                if (finalData != null) {
                    connection.setRequestProperty("Content-Type", hook.getDataTypeHeader());
                    connection.setRequestProperty("Content-Length", Integer.toString(finalData.getBytes().length));
                }
                connection.setUseCaches(false);

                //Send request
                if (finalData != null) {
                    connection.setDoOutput(true);

                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(finalData);
                    wr.close();
                }

                //Get Response
                int code = connection.getResponseCode();
                if (code != 200) {
                    logger.warn("Hook '" + hook.getAddress() + "' responded with code: " + code);
                    return;
                }

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();

                String respString = response.toString().trim();
                if (respString.isEmpty() || respString.equalsIgnoreCase("OK")) return;

                final WebHookResponse resp = new ObjectMapper().readValue(respString, WebHookResponse.class);

                Text msg = resp.getMessage();

                WebAPI.runOnMain(() -> {
                    for (String target : resp.getTargets()) {
                        if (target.equalsIgnoreCase("server")) {
                            Sponge.getServer().getBroadcastChannel().send(msg);
                            continue;
                        }

                        Optional<Player> p = Sponge.getServer().getPlayer(UUID.fromString(target));
                        if (!p.isPresent())
                            continue;

                        p.get().sendMessage(msg);
                    }
                });
            } catch (ConnectException e) {
                logger.warn("Could not connect to hook '" + hook.getAddress() + "': " + e.getMessage());
            } catch (ProtocolException e) {
                logger.warn("Unknown protocol for hook '" + hook.getAddress() + "': " + e.getMessage());
            } catch (MalformedURLException e) {
                logger.warn("Malformed URL for hook '" + hook.getAddress() + "': " + e.getMessage());
            } catch (IOException e) {
                logger.warn("IO Error from hook '" + hook.getAddress() + "': " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }


    // Server events
    @Listener(order = Order.POST)
    public void onServerStart(GameStartedServerEvent event) {
        notifyHooks(WebHookService.WebHookType.SERVER_START, event);
    }
    @Listener(order = Order.PRE)
    public void onServerStop(GameStoppedServerEvent event) {
        notifyHooks(WebHookService.WebHookType.SERVER_STOP, event);
    }

    @Listener(order = Order.POST)
    public void onWorldLoad(LoadWorldEvent event) {
        notifyHooks(WebHookService.WebHookType.WORLD_LOAD, event);
    }
    @Listener(order = Order.POST)
    public void onWorldUnload(UnloadWorldEvent event) {
        notifyHooks(WebHookService.WebHookType.WORLD_UNLOAD, event);
    }
    @Listener(order = Order.POST)
    public void onWorldSave(SaveWorldEvent event) {
        notifyHooks(WebHookService.WebHookType.WORLD_SAVE, event);
    }

    @Listener(order = Order.POST)
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        notifyHooks(WebHookService.WebHookType.PLAYER_JOIN, event);
    }
    @Listener(order = Order.PRE)
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        notifyHooks(WebHookService.WebHookType.PLAYER_LEAVE, event);
    }

    @Listener(order = Order.PRE)
    public void onUserKick(KickPlayerEvent event) {
        notifyHooks(WebHookService.WebHookType.PLAYER_KICK, event);
    }
    @Listener(order = Order.PRE)
    public void onUserBan(BanUserEvent event) {
        notifyHooks(WebHookService.WebHookType.PLAYER_BAN, event);
    }

    @Listener(order = Order.POST)
    public void onEntitySpawn(SpawnEntityEvent event) {
        notifyHooks(WebHookService.WebHookType.ENTITY_SPAWN, event);
    }
    @Listener(order = Order.PRE)
    public void onEntityDespawn(DestructEntityEvent event) {
        Entity ent = event.getTargetEntity();
        if (ent instanceof Player) {
            notifyHooks(WebHookService.WebHookType.PLAYER_DEATH, event);
        } else {
            notifyHooks(WebHookService.WebHookType.ENTITY_DESPAWN, event);
        }
    }
    @Listener(order = Order.PRE)
    public void onEntityExpire(ExpireEntityEvent event) {
        notifyHooks(WebHookService.WebHookType.ENTITY_EXPIRE, event);
    }

    @Listener(order = Order.POST)
    public void onPlayerChat(MessageChannelEvent.Chat event, @First Player player) {
        notifyHooks(WebHookService.WebHookType.CHAT, event);
    }
    @Listener(order = Order.POST)
    public void onMessage(MessageChannelEvent event) {
        notifyHooks(WebHookService.WebHookType.CHAT, event);
    }

    @Listener(order = Order.POST)
    public void onInteractBlock(InteractBlockEvent event) {
        notifyHooks(WebHookService.WebHookType.INTERACT_BLOCK, event);
    }
    @Listener(order = Order.POST)
    public void onInteractInventory(InteractInventoryEvent.Open event) {
        notifyHooks(WebHookService.WebHookType.INVENTORY_OPEN, event);
    }
    @Listener(order = Order.POST)
    public void onInteractInventory(InteractInventoryEvent.Close event) {
        notifyHooks(WebHookService.WebHookType.INVENTORY_CLOSE, event);
    }

    /*@Listener(order = Order.POST)
    public void onPlayerAdvancement(AdvancementEvent.Grant event) {
        notifyHooks(WebHookService.WebHookType.ADVANCEMENT, event);
    }*/

    @Listener(order = Order.POST)
    public void onPlayerAchievement(GrantAchievementEvent.TargetPlayer event) {
        Player player = event.getTargetEntity();

        // Check if we already have the achievement
        if (player.getAchievementData().achievements().get().stream().anyMatch(a -> a.getId().equals(event.getAchievement().getId())))
            return;

        notifyHooks(WebHookService.WebHookType.ACHIEVEMENT, event);
    }

    @Listener(order = Order.POST)
    public void onGenerateChunk(GenerateChunkEvent event) {
        notifyHooks(WebHookService.WebHookType.GENERATE_CHUNK, event);
    }

    @Listener(order = Order.POST)
    public void onExplosion(ExplosionEvent event) {
        notifyHooks(WebHookService.WebHookType.EXPLOSION, event);
    }

    @Listener(order = Order.POST)
    public void onCommand(SendCommandEvent event) {
        notifyHooks(WebHookService.WebHookType.COMMAND, event);
    }

    @Listener(order = Order.POST)
    public void onBlockUpdateStatusChange(BlockOperationStatusChangeEvent event) {
        notifyHooks(WebHookService.WebHookType.BLOCK_OPERATION_STATUS, event);
    }
}
