package valandur.webapi.hook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.*;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.hook.filter.BlockTypeFilter;
import valandur.webapi.hook.filter.ItemTypeFilter;
import valandur.webapi.hook.filter.PlayerFilter;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.Extensions;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class WebHooks {

    public enum WebHookType {
        ALL, CUSTOM_COMMAND, CUSTOM_EVENT, CUSTOM_MESSAGE,
        ACHIEVEMENT, BLOCK_UPDATE_STATUS, CHAT, COMMAND, GENERATE_CHUNK, EXPLOSION, INTERACT_BLOCK, INVENTORY_OPEN,
        INVENTORY_CLOSE, PLAYER_JOIN, PLAYER_LEAVE, PLAYER_DEATH, PLAYER_KICK, PLAYER_BAN, SERVER_START, SERVER_STOP,
        WORLD_SAVE, WORLD_LOAD, WORLD_UNLOAD
    }

    private static Map<String, CommandWebHook> commandHooks = new HashMap<>();
    private static Map<WebHookType, List<WebHook>> eventHooks = new HashMap<>();
    private static Map<Class<? extends Event>, Tuple<List<WebHook>, EventListener>> customHooks = new HashMap<>();
    private static Map<String, Class<? extends WebHookFilter>> filters = new HashMap<>();

    public static Map<String, CommandWebHook> getCommandHooks() {
        return commandHooks;
    }

    private static final String configFileName = "hooks.conf";
    private static String userAgent = WebAPI.NAME + "/" + WebAPI.VERSION;

    public static void init() {
        WebAPI api = WebAPI.getInstance();
        Logger logger = api.getLogger();

        logger.info("Initializing web hooks...");

        // Remove existing listeners to prevent multiple subscriptions on config reload
        for (Tuple<List<WebHook>, EventListener> entry : customHooks.values()) {
            Sponge.getEventManager().unregisterListeners(entry.getSecond());
        }

        // Save some basic data
        Platform platform = Sponge.getPlatform();
        String mc = platform.getContainer(Platform.Component.GAME).getVersion().orElse("?");
        String sponge = platform.getContainer(Platform.Component.IMPLEMENTATION).getVersion().orElse("?");
        userAgent = WebAPI.NAME + "/" + WebAPI.VERSION + " Sponge/" + sponge + " Minecraft/" + mc + " Java/" + System.getProperty("java.version");

        // Clear hooks
        commandHooks.clear();
        eventHooks.clear();
        customHooks.clear();

        // Load filters
        logger.info("Loading filters...");

        filters.clear();

        // Add some default filters
        filters.put(BlockTypeFilter.name, BlockTypeFilter.class);
        filters.put(PlayerFilter.name, PlayerFilter.class);
        filters.put(ItemTypeFilter.name, ItemTypeFilter.class);

        // Load custom filters
        Extensions.loadPlugins("filters", WebHookFilter.class, filterClass -> {
            try {
                String name = (String) filterClass.getField("name").get(null);
                filterClass.getConstructor(WebHook.class, ConfigurationNode.class);
                filters.put(name, filterClass);
            } catch (NoSuchMethodException e) {
                logger.error("   Requires a constructor like so: (WebHook hook, ConfigurationNode node)");
            } catch (NoSuchFieldException | IllegalAccessException e) {
                logger.error("   Requires a 'public static String name' field");
            }
        });

        logger.info("Done loading filters");

        // Load config
        Tuple<ConfigurationLoader, ConfigurationNode> tup = api.loadWithDefaults(configFileName, "defaults/" + configFileName);
        ConfigurationNode config = tup.getSecond();

        try {
            logger.info("Loading command hooks...");

            // Add command hooks
            Map<Object, ? extends ConfigurationNode> cmdMap = config.getNode("command").getChildrenMap();
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : cmdMap.entrySet()) {
                CommandWebHook hook = entry.getValue().getValue(TypeToken.of(CommandWebHook.class));
                commandHooks.put(entry.getKey().toString(), hook);
            }

            logger.info("Loading event hooks...");

            // Add event hooks (this also adds the "all" hooks)
            ConfigurationNode eventNode = config.getNode("events");
            for (WebHookType type : WebHookType.values()) {
                // Skip the custom hooks
                if (type == WebHookType.CUSTOM_COMMAND || type == WebHookType.CUSTOM_EVENT)
                    continue;

                List<WebHook> hooks = eventNode.getNode(type.toString().toLowerCase()).getList(TypeToken.of(WebHook.class));
                eventHooks.put(type, hooks.stream().filter(WebHook::isEnabled).collect(Collectors.toList()));
            }

            logger.info("Loading custom event hooks...");

            // Add custom event hooks
            Map<Object, ? extends ConfigurationNode> customMap = config.getNode("custom").getChildrenMap();
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : customMap.entrySet()) {
                Class c = Class.forName(entry.getKey().toString());
                if (!Event.class.isAssignableFrom(c))
                    throw new ClassNotFoundException("Class " + c.toString() + " must be a subclass of " + Event.class.toString());
                Class<? extends Event> clazz = (Class<? extends Event>)c;

                WebHookEventListener listener = new WebHookEventListener(clazz);
                List<WebHook> hooks = entry.getValue().getList(TypeToken.of(WebHook.class));

                Sponge.getEventManager().registerListener(api, clazz, listener);
                customHooks.put(clazz, new Tuple<>(hooks.stream().filter(WebHook::isEnabled).collect(Collectors.toList()), listener));
            }
        } catch (ObjectMappingException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Optional<Class<? extends WebHookFilter>> getFilter(String name) {
        return filters.containsKey(name) ? Optional.of(filters.get(name)) : Optional.empty();
    }

    public static void notifyHooks(WebHookType type, Object data) {
        List<WebHook> notifyHooks = new ArrayList<>(eventHooks.get(type));
        if (type != WebHookType.CUSTOM_MESSAGE) {
            notifyHooks.addAll(eventHooks.get(WebHookType.ALL));
        }
        for (WebHook hook : notifyHooks) {
            notifyHook(hook, type, null, new HashMap<>(), data);
        }
    }
    public static void notifyHook(CommandWebHook cmdHook, String source, Map<String, Tuple<String, Object>> data) {
        Map<String, String> params = data.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getFirst()));
        Map<String, Object> body = data.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getSecond()));

        for (WebHook hook : cmdHook.getHooks()) {
            notifyHook(hook, WebHookType.CUSTOM_COMMAND, source, params, body);
        }
    }
    public static void notifyHooks(Class<? extends Event> clazz, Object data) {
        List<WebHook> notifyHooks = new ArrayList<>(customHooks.get(clazz).getFirst());
        for (WebHook hook : notifyHooks) {
            notifyHook(hook, WebHookType.CUSTOM_EVENT, null, new HashMap<>(), data);
        }
    }

    private static void notifyHook(WebHook hook, WebHookType eventType, String source, Map<String, String> params, Object data) {
        // First check the filter before we do any processing
        if (hook.getFilter() != null && !hook.getFilter().process(data)) {
            return;
        }

        String address = hook.getAddress();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            address = address.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        final String finalAddress = address;

        String stringData = JsonConverter.toString(data, hook.includeDetails(), hook.getPermissions());
        if (data != null) {
            try {
                stringData = hook.getDataType() == WebHook.WebHookDataType.JSON ? stringData : "body=" + URLEncoder.encode(stringData, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final String finalData = stringData;

        final Logger logger = WebAPI.getInstance().getLogger();
        CompletableFuture.runAsync(() -> {
            HttpURLConnection connection = null;
            try {
                //Create connection
                URL url = new URL(finalAddress);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(hook.getMethod().toString());
                for (WebHookHeader header : hook.getHeaders()) {
                    String val = header.getValue();
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        val = val.replace("{" + entry.getKey() + "}", entry.getValue());
                    }
                    connection.setRequestProperty(header.getName(), val);
                }
                connection.setRequestProperty("User-Agent", userAgent);
                connection.setRequestProperty("X-WebAPI-Version", WebAPI.VERSION);
                connection.setRequestProperty("X-WebAPI-Event", eventType.toString());
                if (source != null) connection.setRequestProperty("X-WebAPI-Source", source);
                connection.setRequestProperty("accept", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                if (finalData != null && hook.getMethod() != WebHook.WebHookMethod.GET) {
                    connection.setRequestProperty("Content-Type", hook.getDataTypeHeader());
                    connection.setRequestProperty("Content-Length", Integer.toString(finalData.getBytes().length));
                }
                connection.setUseCaches(false);

                //Send request
                if (finalData != null) {
                    if (hook.getMethod() != WebHook.WebHookMethod.GET) {
                        connection.setDoOutput(true);

                        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                        wr.writeBytes(finalData);
                        wr.close();
                    } else {
                        logger.warn("Hook '" + hook.getAddress() + " will not receive data because it uses 'GET' method");
                    }
                }

                //Get Response
                int code = connection.getResponseCode();
                if (code != 200) {
                    WebAPI.getInstance().getLogger().warn("Hook '" + hook.getAddress() + "' responded with code: " + code);
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
}
