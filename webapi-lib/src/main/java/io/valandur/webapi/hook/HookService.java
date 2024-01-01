package io.valandur.webapi.hook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;
import io.valandur.webapi.hook.event.EventData;
import io.valandur.webapi.web.ObjectMapperProvider;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class HookService<T extends WebAPI<?, ?>> extends Service<T> {

    protected String userAgent = null;
    protected Map<HookEventType, List<EventHook>> eventHooks;
    protected Map<String, CommandHook> commandHooks;
    protected ObjectMapper mapper = ObjectMapperProvider.createDefaultObjectMapper();

    public HookService(T webapi) {
        super(webapi);
    }

    @Override
    public void init() {
        super.init();

        var config = webapi.getHookConfig();
        try {
            config.load();
        } catch (Exception e) {
            webapi.getLogger().error("Could not load config: " + e.getMessage());
        }

        eventHooks = config.getEventHooks().stream()
                .collect(Collectors.groupingBy(h -> h.eventType));
        commandHooks = config.getCommandHooks().stream()
                .flatMap(h -> h.aliases.stream().map(a -> new SimpleEntry<>(a, h)))
                .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));

        try {
            config.save();
        } catch (Exception e) {
            webapi.getLogger().error("Could not save config: " + e.getMessage());
        }
    }

    public void notifyEventHooks(EventData data) {
        List<EventHook> notifyHooks = new ArrayList<>();

        List<EventHook> origHooks = eventHooks.get(data.event());
        if (origHooks != null) {
            notifyHooks.addAll(origHooks);
        }

        List<EventHook> allHooks = eventHooks.get(HookEventType.ALL);
        if (allHooks != null) {
            notifyHooks.addAll(allHooks);
        }

        for (var hook : notifyHooks) {
            if (hook.isEnabled()) {
                notifyHook(hook, null, data);
            }
        }
    }

    public void notifyCommandHook(CommandHook cmdHook, String source, Object data) {
        if (cmdHook.isEnabled()) {
            notifyHook(cmdHook, source, null);
        }
    }

    private void notifyHook(Hook hook, String source, EventData data) {
        if (userAgent == null) {
            var info = infoService.getInfo();
            userAgent = WebAPI.NAME + "/" + webapi.getVersion() +
                    " " + info.flavour() + "/" + info.flavourVersion() +
                    " Minecraft/" + info.minecraftVersion() +
                    " Java/" + System.getProperty("java.version");
        }

        final String address = hook.getAddress();

        String stringData = null;
        try {
            stringData = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.warn("Hook error: " + e);
        }

        final String finalData = stringData;

        webapi.runAsync(() -> {
            HttpURLConnection connection = null;
            try {
                //Create connection
                URL url = URI.create(address).toURL();
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(hook.getMethod());
                for (var header : hook.getHeaders()) {
                    connection.setRequestProperty(header.name(), header.value());
                }
                connection.setRequestProperty("User-Agent", userAgent);
                connection.setRequestProperty("X-WebAPI-Version", webapi.getVersion());
                connection.setRequestProperty("X-WebAPI-Event", data.event().toString());
                if (source != null) {
                    connection.setRequestProperty("X-WebAPI-Source", source);
                }
                connection.setRequestProperty("Accept", "application/json, application/xml");
                connection.setRequestProperty("Charset", StandardCharsets.UTF_8.name());
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
                if (respString.isEmpty() || respString.equalsIgnoreCase("OK")) {
                    return;
                }

                final HookResponse resp = new ObjectMapper().readValue(respString, HookResponse.class);

                /*Text msg = resp.getMessage();

                webapi.runOnMain(() -> {
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
                });*/
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
