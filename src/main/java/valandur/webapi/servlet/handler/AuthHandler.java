package valandur.webapi.servlet.handler;

import com.google.common.net.HttpHeaders;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.permission.PermissionService;
import valandur.webapi.permission.PermissionStruct;
import valandur.webapi.user.UserPermission;
import valandur.webapi.util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthHandler extends AbstractHandler {

    public static final String API_KEY_HEADER = "X-WEBAPI-KEY";
    private static final String defaultKey = "__DEFAULT__";
    private static final String configFileName = "permissions.conf";

    private static String ACCESS_CONTROL_ORIGIN = "*";
    private static final String ACCESS_CONTROL_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
    private static final String ACCESS_CONTROL_HEADERS = "Origin,Content-Type,Accept,X-Forwarded-For," + API_KEY_HEADER;

    private ConfigurationLoader loader;
    private ConfigurationNode config;
    private PermissionService permissionService;

    private PermissionStruct defaultPerms;
    private Map<String, PermissionStruct> permMap = new HashMap<>();

    private Map<String, UserPermission> tempPermMap = new HashMap<>();

    private boolean useWhitelist;
    private List<String> whitelist = new ArrayList<>();

    private boolean useBlacklist;
    private List<String> blacklist = new ArrayList<>();

    private List<String> allowedProxies = new ArrayList<>();


    public AuthHandler() {
        permissionService = WebAPI.getPermissionService();
    }

    public void addTempPerm(String key, UserPermission perms) {
        tempPermMap.put(key, perms);
    }

    public void init() {
        Logger logger = WebAPI.getLogger();
        logger.info("Loading keys & permissions...");

        Tuple<ConfigurationLoader, ConfigurationNode> tup =
                Util.loadWithDefaults(configFileName, "defaults/" + configFileName);
        loader = tup.getFirst();
        config = tup.getSecond();

        TreeNode<String, Boolean> defs = permissionService.permissionTreeFromConfig(config.getNode("default", "permissions"));
        int defLimit = config.getNode("default", "rateLimit").getInt();
        defaultPerms = new PermissionStruct(defs, defLimit);

        permMap.clear();
        for (ConfigurationNode node : config.getNode("keys").getChildrenList()) {
            String key = node.getNode("key").getString();
            if (!node.getNode("enabled").getBoolean()) {
                logger.info("Skipping disabled key " + key);
                continue;
            }
            if (key == null || key.isEmpty()) {
                logger.error("SKIPPING KEY-PERMISSION MAPPING WITH INVALID KEY");
                continue;
            }
            if (key.equalsIgnoreCase("ADMIN") || key.equalsIgnoreCase("USER") ||
                    key.equalsIgnoreCase("7S%M2FYp9NYT^Ozg")) {
                logger.error("YOU STILL HAVE SOME DEFAULT KEYS IN YOUR PERMSSIONS.CONF! " +
                        "PLESAE CHANGE OR DEACTIVATE THEM IMMEDIATELY!");
                logger.error("THE KEY '" + key + "' WILL BE SKIPPED!");
                continue;
            }
            if (key.length() < 8) {
                logger.error("YOU HAVE A KEY WITH LESS THAN 8 CHARACTERS! KEYS ARE RECOMMNDED TO BE AT " +
                        "LEAST 16 CHARACTERS AND RANDOMLY GENERATED!");
                logger.error("THE KEY '" + key + "' WILL BE SKIPPED!");
                continue;
            }
            TreeNode<String, Boolean> perms = permissionService.permissionTreeFromConfig(node.getNode("permissions"));
            int rateLimit = node.getNode("rateLimit").getInt();
            permMap.put(key, new PermissionStruct(perms, rateLimit));
        }

        try {
            // Load whitelist
            useWhitelist = config.getNode("useWhitelist").getBoolean();
            whitelist = config.getNode("whitelist").getList(TypeToken.of(String.class));

            // Load blacklist
            useBlacklist = config.getNode("useBlacklist").getBoolean();
            blacklist = config.getNode("blacklist").getList(TypeToken.of(String.class));

            // Allowed proxies
            allowedProxies = config.getNode("allowedProxies").getList(TypeToken.of(String.class));
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }

        ACCESS_CONTROL_ORIGIN = config.getNode("accessControlOrigin").getString(ACCESS_CONTROL_ORIGIN);
    }

    private void setAndSaveConfig(String node, Object value) {
        config.getNode(node).setValue(value);

        try {
            loader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
            if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
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
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String addr = getRealAddr(request);

        if (useWhitelist && !whitelist.contains(addr)) {
            WebAPI.getLogger().warn(addr + " is not on whitelist: " + target);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            baseRequest.setHandled(true);
            return;
        } else if (useBlacklist && blacklist.contains(addr)) {
            WebAPI.getLogger().warn(addr + " is on blacklist: " + target);
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            baseRequest.setHandled(true);
            return;
        }

        String key = request.getHeader(API_KEY_HEADER);

        if (key == null && request.getQueryString() != null) {
            Map<String, String> query = Util.getQueryParams(request);
            key = query.get("key");
        }

        PermissionStruct perms = permMap.get(key);
        if (perms != null) {
            request.setAttribute("key", key);
            request.setAttribute("perms", perms.getPermissions());
            request.setAttribute("rate", perms.getRateLimit());
        } else {
            UserPermission uPerms = tempPermMap.get(key);
            if (uPerms != null) {
                request.setAttribute("key", key);
                request.setAttribute("perms", uPerms.getPermissions());
                request.setAttribute("rate", 0);
                request.setAttribute("user", uPerms);
            } else {
                request.setAttribute("key", defaultKey);
                request.setAttribute("perms", defaultPerms.getPermissions());
                request.setAttribute("rate", defaultPerms.getRateLimit());
            }
        }

        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ACCESS_CONTROL_ORIGIN);
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_METHODS);
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_HEADERS);
    }

    private String getRealAddr(HttpServletRequest request) {
        final String addr = request.getRemoteAddr();
        String forwardedFor = request.getHeader(HttpHeaders.X_FORWARDED_FOR);
        if (forwardedFor == null)
            return addr;

        // First check the actual IP that we got. If that is not a trusted proxy we're done.
        if (!allowedProxies.contains(addr)) {
            WebAPI.getLogger().warn(addr + " sent " + HttpHeaders.X_FORWARDED_FOR +
                    " header, but is not a proxy. Header will be ignored!");
            return addr;
        }

        String[] ips = forwardedFor.split(",");

        // Traverse the X-Forwarded-For header backwards and take the first IP that we don't trust.
        for (int i = ips.length - 1; i >= 0; i--) {
            if (!allowedProxies.contains(ips[i].trim())) {
                if (i > 0) {
                    WebAPI.getLogger().warn(ips[i].trim() + " sent " + HttpHeaders.X_FORWARDED_FOR +
                            " header, but is not a proxy. Header will be ignored!");
                }
                return ips[i];
            }
        }

        return null;
    }
}
