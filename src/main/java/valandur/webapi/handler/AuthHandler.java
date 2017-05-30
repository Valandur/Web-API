package valandur.webapi.handler;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.misc.TreeNode;
import valandur.webapi.misc.Util;
import valandur.webapi.permission.PermissionStruct;
import valandur.webapi.permission.Permissions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthHandler extends AbstractHandler {

    private static final String defaultKey = "__DEFAULT__";
    private static final String configFileName = "permissions.conf";

    private WebAPI api;
    private ConfigurationLoader loader;
    private ConfigurationNode config;

    private PermissionStruct defaultPerms;
    private Map<String, PermissionStruct> permMap = new HashMap<>();

    private boolean useWhitelist;
    private List<String> whitelist = new ArrayList<>();

    private boolean useBlacklist;
    private List<String> blacklist = new ArrayList<>();


    public AuthHandler() {
        api = WebAPI.getInstance();
    }

    public void init() {
        Tuple<ConfigurationLoader, ConfigurationNode> tup = api.loadWithDefaults(configFileName, "defaults/" + configFileName);
        loader = tup.getFirst();
        config = tup.getSecond();

        TreeNode<String, Boolean> defs = Permissions.permissionTreeFromConfig(config.getNode("default", "permissions"));
        int defLimit = config.getNode("default", "rateLimit").getInt();
        defaultPerms = new PermissionStruct(defs, defLimit);

        for (ConfigurationNode node : config.getNode("keys").getChildrenList()) {
            String key = node.getNode("key").getString();
            if (key == null || key.isEmpty()) {
                api.getLogger().warn("SKIPPING KEY-PERMISSION MAPPING WITH INVALID KEY");
                continue;
            }
            TreeNode<String, Boolean> perms = Permissions.permissionTreeFromConfig(node.getNode("permissions"));
            int rateLimit = node.getNode("rateLimit").getInt();
            permMap.put(key, new PermissionStruct(perms, rateLimit));
        }

        useWhitelist = config.getNode("useWhitelist").getBoolean();
        for (ConfigurationNode node : config.getNode("whitelist").getChildrenList()) {
            whitelist.add(node.getString());
        }

        useBlacklist = config.getNode("useBlacklist").getBoolean();
        for (ConfigurationNode node : config.getNode("blacklist").getChildrenList()) {
            blacklist.add(node.getString());
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
            request.setAttribute("perms", defaultPerms.getPermissions());
            request.setAttribute("rate", defaultPerms.getRateLimit());
        }
    }
}
