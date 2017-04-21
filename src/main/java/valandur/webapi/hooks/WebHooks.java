package valandur.webapi.hooks;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.*;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.json.JsonConverter;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class WebHooks {

    public enum WebHookType {
        ALL, CUSTOM_COMMAND, CUSTOM_EVENT, ACHIEVEMENT, CHAT, COMMAND, INVENTORY_OPEN, INVENTORY_CLOSE,
        PLAYER_JOIN, PLAYER_LEAVE, PLAYER_DEATH, PLAYER_KICK, PLAYER_BAN, SERVER_START, SERVER_STOP,
    }

    private static Map<String, CommandWebHook> commandHooks = new HashMap<>();
    private static Map<WebHookType, List<WebHook>> eventHooks = new HashMap<>();
    private static Map<Class<? extends Event>, Tuple<List<WebHook>, EventListener>> customHooks = new HashMap<>();

    public static Map<String, CommandWebHook> getCommandHooks() {
        return commandHooks;
    }

    private static final String configFileName = "hooks.conf";
    private static String userAgent = WebAPI.NAME + "/" + WebAPI.VERSION;

    public static void reloadConfig() {
        WebAPI api = WebAPI.getInstance();

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

        // Load config
        Tuple<ConfigurationLoader, ConfigurationNode> tup = api.loadWithDefaults(configFileName, "defaults/" + configFileName);
        ConfigurationNode config = tup.getSecond();

        try {
            // Add command hooks
            Map<Object, ? extends ConfigurationNode> cmdMap = config.getNode("command").getChildrenMap();
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : cmdMap.entrySet()) {
                CommandWebHook hook = entry.getValue().getValue(TypeToken.of(CommandWebHook.class));
                commandHooks.put(entry.getKey().toString(), hook);
            }

            // Add event hooks (this also adds the "all" hooks)
            ConfigurationNode eventNode = config.getNode("events");
            for (WebHookType type : WebHookType.values()) {
                // Skip the custom hooks
                if (type == WebHookType.CUSTOM_COMMAND || type == WebHookType.CUSTOM_EVENT)
                    continue;

                List<WebHook> hooks = eventNode.getNode(type.toString().toLowerCase()).getList(TypeToken.of(WebHook.class));
                eventHooks.put(type, hooks.stream().filter(WebHook::isEnabled).collect(Collectors.toList()));
            }

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

    public static void notifyHooks(WebHookType type, String message) {
        List<WebHook> notifyHooks = new ArrayList<>(eventHooks.get(type));
        notifyHooks.addAll(eventHooks.get(WebHookType.ALL));
        for (WebHook hook : notifyHooks) {
            notifyHook(hook, type, null, new HashMap<>(), message);
        }
    }
    public static void notifyHook(String name, String source, Map<String, Tuple<String, JsonNode>> params) {
        CommandWebHook cmdHook = commandHooks.get(name);
        Map<String, JsonNode> contentMap = params.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getSecond()));
        for (WebHook hook : cmdHook.getHooks()) {
            notifyHook(hook, WebHookType.CUSTOM_COMMAND, source, params, JsonConverter.toString(contentMap, true));
        }
    }
    public static void notifyHooks(Class<? extends Event> clazz, String message) {
        List<WebHook> notifyHooks = new ArrayList<>(customHooks.get(clazz).getFirst());
        for (WebHook hook : notifyHooks) {
            notifyHook(hook, WebHookType.CUSTOM_EVENT, null, new HashMap<>(), message);
        }
    }

    private static void notifyHook(WebHook hook, WebHookType eventType, String source, Map<String, Tuple<String, JsonNode>> params, String content) {
        String address = hook.getAddress();
        for (Map.Entry<String, Tuple<String, JsonNode>> entry : params.entrySet()) {
            address = address.replace("{" + entry.getKey() + "}", entry.getValue().getFirst());
        }
        final String finalAddress = address;

        String data = null;
        if (content != null) {
            try {
                data = hook.getDataType() == WebHook.WebHookDataType.JSON ? content : "body=" + URLEncoder.encode(content, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final String finalData = data;

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
                    for (Map.Entry<String, Tuple<String, JsonNode>> entry : params.entrySet()) {
                        val = val.replace("{" + entry.getKey() + "}", entry.getValue().getFirst());
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
                } else {
                    InputStream is = connection.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    WebAPI.getInstance().getLogger().info(hook.getAddress() + ": " + response.toString());
                }
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
