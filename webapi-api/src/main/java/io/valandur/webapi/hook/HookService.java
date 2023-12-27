package io.valandur.webapi.hook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.valandur.webapi.Service;
import io.valandur.webapi.WebAPI;
import java.util.AbstractMap.SimpleEntry;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public abstract class HookService<T extends WebAPI<?>> extends Service<T> {

    protected Map<HookEventType, List<EventHook>> eventHooks;
    protected Map<String, CommandHook> commandHooks;

    public HookService(T webapi) {
        super(webapi);

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

    public void notifyEventHooks(HookEventType type, Object data) {
        List<EventHook> notifyHooks = new ArrayList<>();

        List<EventHook> origHooks = eventHooks.get(type);
        if (origHooks != null) {
            notifyHooks.addAll(origHooks);
        }

        List<EventHook> allHooks = eventHooks.get(HookEventType.ALL);
        if (allHooks != null) {
            notifyHooks.addAll(allHooks);
        }

        for (var hook : notifyHooks) {
            notifyHook(hook, type, null, data);
        }
    }
    public void notifyCommandHook(CommandHook cmdHook, String source, Object data) {
        notifyHook(cmdHook, HookEventType.CUSTOM_COMMAND, source, data);
    }

    private void notifyHook(Hook hook, HookEventType eventType, String source, Object data) {
        final String address = hook.getAddress();

        String stringData = "";
        try {
            ObjectMapper om = WebAPI.getSerializeService().getDefaultObjectMapper(
                    hook.getDataType() == WebHook.WebHookDataType.XML,
                    hook.includeDetails(),
                    hook.getPermissions()
            );
            stringData = om.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            logger.warn("Hook error: " + e);
        }

        if (data != null) {
            try {
                stringData = hook.isForm() ? "body=" + URLEncoder.encode(stringData, "UTF-8") : stringData;
            } catch (Exception e) {
                logger.warn("Hook error: " + e);
            }
        }

        final String finalData = stringData;

        // We actually want to run this async
        webapi.runOnMain(() -> {
            HttpURLConnection connection = null;
            try {
                //Create connection
                URL url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(hook.getMethod());
                for (var header : hook.getHeaders()) {
                    connection.setRequestProperty(header.getName(), header.getValue());
                }
                connection.setRequestProperty("User-Agent", userAgent);
                connection.setRequestProperty("X-WebAPI-Version", webapi.getVersion());
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

                final HookResponse resp = new ObjectMapper().readValue(respString, HookResponse.class);

                Text msg = resp.getMessage();

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
