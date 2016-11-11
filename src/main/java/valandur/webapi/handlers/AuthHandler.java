package valandur.webapi.handlers;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.PathWatcher;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import valandur.webapi.WebAPI;
import valandur.webapi.misc.Util;
import valandur.webapi.servlets.APIServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthHandler extends AbstractHandler {
    private static final String configFileName = "permissions.conf";

    private WebAPI api;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode config;

    List<String> defaultPerms = new ArrayList<>();
    private Map<String, List<String>> permMap = new HashMap<>();

    private boolean useWhitelist;
    private List<String> whitelist = new ArrayList<>();

    private boolean useBlacklist;
    private List<String> blacklist = new ArrayList<>();


    public AuthHandler() {
        api = WebAPI.getInstance();

        try {
            Path configPath = api.getConfigPath().resolve(configFileName);
            if (!Files.exists(configPath))
                Sponge.getAssetManager().getAsset(api, "defaults/" + configFileName).get().copyToDirectory(api.getConfigPath());

            loader = HoconConfigurationLoader.builder().setPath(configPath).build();
            config = loader.load();

            for (ConfigurationNode node : config.getNode("defaultPermissions").getChildrenList()) {
                defaultPerms.add(node.getString());
            }

            for (ConfigurationNode node : config.getNode("keys").getChildrenList()) {
                String token = node.getNode("token").getString();
                List<String> perms = node.getNode("permissions").getList(item -> item.toString(), new ArrayList<>());
                permMap.put(token, perms);
            }

            useWhitelist = config.getNode("useWhitelist").getBoolean(false);
            for (ConfigurationNode node : config.getNode("whitelist").getChildrenList()) {
                whitelist.add(node.getString());
            }

            useBlacklist = config.getNode("useBlacklist").getBoolean(true);
            for (ConfigurationNode node : config.getNode("blacklist").getChildrenList()) {
                blacklist.add(node.getString());
            }

            loader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setAndSaveConfig(String node, Object value) {
        config.getNode(node).setValue(value);
        try {
            loader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleBlacklist(boolean enable) {
        useBlacklist = enable;
        setAndSaveConfig("useBlacklist", enable);
    }
    public void addToBlacklist(String ip) {
        blacklist.add(ip);
        setAndSaveConfig("blacklist", blacklist);
    }
    public void removeFromBlacklist(String ip) {
        blacklist.remove(ip);
        setAndSaveConfig("blacklist", blacklist);
    }

    public void toggleWhitelist(boolean enable) {
        useWhitelist = enable;
        setAndSaveConfig("useWhitelist", enable);
    }
    public void addToWhitelist(String ip) {
        whitelist.add(ip);
        setAndSaveConfig("whitelist", whitelist);
    }
    public void removeFromWhitelist(String ip) {
        whitelist.remove(ip);
        setAndSaveConfig("whitelist", whitelist);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String addr = request.getRemoteAddr();
        if (useWhitelist && !whitelist.contains(addr)) {
            api.getLogger().warn(addr + " is not on whitelist: " + target);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            baseRequest.setHandled(true);
            return;
        } else if (useBlacklist && blacklist.contains(addr)) {
            api.getLogger().warn(addr + " is on blacklist: " + target);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            baseRequest.setHandled(true);
            return;
        }

        String key = request.getHeader("x-webapi-key");

        if (key == null && request.getQueryString() != null) {
            Map<String, String> query = Util.getQueryParts(request);
            key = query.get("key");
        }

        List<String> perms = permMap.get(key);
        if (perms != null) {
            request.setAttribute("perms", perms);
        } else {
            request.setAttribute("perms", defaultPerms);
        }
    }
}
