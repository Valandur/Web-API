package valandur.webapi.handlers;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.Sponge;
import valandur.webapi.WebAPI;
import valandur.webapi.misc.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthHandler extends AbstractHandler {

    private static final String defaultKey = "__DEFAULT__";
    private static final String configFileName = "permissions.conf";

    private WebAPI api;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private ConfigurationNode config;

    private PermissionStruct defaultPerms;
    private Map<String, PermissionStruct> permMap = new HashMap<>();

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

            List<String> defs = new ArrayList<>();
            for (ConfigurationNode node : config.getNode("default", "permissions").getChildrenList()) {
                if (node.getString() == "*") {
                    api.getLogger().warn("DEFAULT PERMISSIONS GRANT UNRESTRICTED ACCESS TO THE API! THIS CAN BE DANGEROUS IF NOT RUNNING ON LOCALHOST!");
                }
                defs.add(node.getString());
            }
            int defLimit = config.getNode("default", "rateLimit").getInt(0);
            defaultPerms = new PermissionStruct(defs, defLimit);

            for (ConfigurationNode node : config.getNode("keys").getChildrenList()) {
                String token = node.getNode("key").getString();
                if (token == null || token.isEmpty()) {
                    api.getLogger().warn("SKIPPING KEY-PERMISSION MAPPING WITH INVALID KEY");
                    continue;
                }
                List<String> perms = node.getNode("permissions").getList(item -> item.toString(), new ArrayList<>());
                int rateLimit = node.getNode("rateLimit").getInt(0);
                permMap.put(token, new PermissionStruct(perms, rateLimit));
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

        PermissionStruct perms = permMap.get(key);
        if (perms != null) {
            request.setAttribute("key", key);
            request.setAttribute("perms", perms.getPermissions());
            request.setAttribute("rate", perms.getRateLimit());
        } else {
            request.setAttribute("key", defaultKey);
            request.setAttribute("perms", defaultPerms);
            request.setAttribute("rate", perms.getRateLimit());
        }
    }


    public static class PermissionStruct {
        private List<String> permissions;
        public List<String> getPermissions() {
            return permissions;
        }

        private int rateLimit;
        public int getRateLimit() {
            return rateLimit;
        }

        public PermissionStruct(List<String> permissions, int rateLimit) {
            this.permissions = permissions;
            this.rateLimit = rateLimit;
        }
    }
}
