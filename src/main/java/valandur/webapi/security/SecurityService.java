package valandur.webapi.security;

import ninja.leaping.configurate.ConfigurationNode;
import org.slf4j.Logger;
import valandur.webapi.WebAPI;
import valandur.webapi.config.BaseConfig;
import valandur.webapi.config.PermissionConfig;
import valandur.webapi.user.UserPermissionStruct;
import valandur.webapi.util.SubnetUtils;
import valandur.webapi.util.TreeNode;

import javax.ws.rs.ForbiddenException;
import java.util.*;

/**
 * The security service handles access permissions to routes within the Web-API.
 */
public class SecurityService {

    private static final String configFileName = "permissions.conf";

    public static final String API_KEY_HEADER = "X-WEBAPI-KEY";
    public static final String DEFAULT_KEY = "__DEFAULT__";

    public static String ACCESS_CONTROL_ORIGIN = "*";
    public static final String ACCESS_CONTROL_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
    public static final String ACCESS_CONTROL_HEADERS = "origin, content-type, x-webapi-key";

    private Set<String> allowedProxyIps = new HashSet<>();
    private Set<SubnetUtils.SubnetInfo> allowedProxyCidrs = new HashSet<>();

    private long start = System.nanoTime();

    private PermissionConfig config;

    private PermissionStruct defaultPerms;
    private Map<String, PermissionStruct> permMap = new HashMap<>();

    private Map<String, List<String>> tempUsersKeyMap = new HashMap<>();
    private Map<String, UserPermissionStruct> tempKeyMap = new HashMap<>();


    public void init() {
        Logger logger = WebAPI.getLogger();
        logger.info("Loading keys & permissions...");

        start = System.nanoTime();

        java.nio.file.Path configPath = WebAPI.getConfigPath().resolve(configFileName).normalize();
        config = BaseConfig.load(configPath, new PermissionConfig());
        defaultPerms = config.def;

        for (String proxy : config.allowedProxies) {
            if (proxy.contains("/")) {
                SubnetUtils utils = new SubnetUtils(proxy);
                utils.setInclusiveHostCount(true);
                allowedProxyCidrs.add(utils.getInfo());
            } else {
                allowedProxyIps.add(proxy);
            }
        }

        permMap.clear();
        for (Map.Entry<String, PermissionStruct> entry : config.keys.entrySet()) {
            String key = entry.getKey();
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
            permMap.put(key, entry.getValue());
        }

        ACCESS_CONTROL_ORIGIN = config.accessControlOrigin;
    }

    public boolean whitelistContains(String addr) {
        return !config.useWhitelist || config.whitelist.contains(addr);
    }
    public boolean blacklistContains(String addr) {
        return !config.useBlacklist || config.blacklist.contains(addr);
    }

    public PermissionStruct getDefaultPermissions() {
        return defaultPerms;
    }
    public PermissionStruct getPermissions(String key) {
        PermissionStruct permStruct = permMap.get(key);
        if (permStruct == null) {
            permStruct = tempKeyMap.get(key);
        }
        // If the user provided a key and it's invalid, then throw an exception
        if (permStruct == null) {
            throw new ForbiddenException("Invalid api key");
        } else {
            // Makes sure that we have the correct key in case this PermStruct
            // is from a logged-in user, who usually only has a username and password.
            permStruct = permStruct.withKey(key);
        }
        return permStruct;
    }

    public boolean containsProxyIP(String ip) {
        return allowedProxyIps.contains(ip) || allowedProxyCidrs.stream().anyMatch(c -> c.isInRange(ip));
    }

    public void addTempKey(String key, UserPermissionStruct user) {
        if (!tempUsersKeyMap.containsKey(user.getName())) {
            tempUsersKeyMap.put(user.getName(), new ArrayList<>());
        }
        tempUsersKeyMap.get(user.getName()).add(key);
        tempKeyMap.put(key, user);
    }
    public void removeTempKey(String key) {
        UserPermissionStruct user = tempKeyMap.remove(key);
        if (user != null) {
            tempUsersKeyMap.get(user.getName()).remove(key);
        }
    }
    public void updateAllFrom(UserPermissionStruct user) {
        List<String> keys = tempUsersKeyMap.get(user.getName());
        if (keys != null) {
            keys.forEach(k -> tempKeyMap.put(k, user));
        }
    }
    public void removeAllFrom(String username) {
        List<String> keys = tempUsersKeyMap.remove(username);
        if (keys != null) {
            keys.forEach(k -> tempKeyMap.remove(k));
        }
    }

    public void toggleBlacklist(boolean enable) {
        config.useBlacklist = enable;
        config.save();
    }
    public void addToBlacklist(String ip) {
        config.blacklist.add(ip);
        config.save();
    }
    public void removeFromBlacklist(String ip) {
        config.blacklist.remove(ip);
        config.save();
    }

    public void toggleWhitelist(boolean enable) {
        config.useWhitelist = enable;
        config.save();
    }
    public void addToWhitelist(String ip) {
        config.whitelist.add(ip);
        config.save();
    }
    public void removeFromWhitelist(String ip) {
        config.whitelist.remove(ip);
        config.save();
    }



    /**
     * Returns a permissions tree representing the passed configuration node
     * @param config The configuration node which represents a permission set
     * @return A tree structure representing the config node
     */
    public static TreeNode permissionTreeFromConfig(ConfigurationNode config) {
        if (config == null || config.getValue() == null) {
            return new TreeNode(false);
        }

        if (!config.hasMapChildren()) {
            if (config.getValue().getClass() == Boolean.class)
                return new TreeNode(config.getKey().toString(), config.getBoolean());
            else {
                TreeNode node = new TreeNode(config.getKey().toString(), true);
                node.addChild(new TreeNode("*", true));
                return node;
            }
        }

        TreeNode root = new TreeNode(config.getKey().toString(), true);
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : config.getChildrenMap().entrySet()) {
            if (entry.getKey().toString().equalsIgnoreCase(".")) {
                root.setValue(entry.getValue().getBoolean());
                continue;
            }

            root.addChild(permissionTreeFromConfig(entry.getValue()));
        }
        return root;
    }

    /**
     * Adds the given permission tree as a config node to the passed base node.
     * @param config The base config node, at which the permissions tree is added
     * @param perms The permissions tree which is parsed to a config node
     */
    public static void permissionTreeToConfig(ConfigurationNode config, TreeNode perms) {
        if (perms == null) {
            return;
        }

        Collection<TreeNode> children = perms.getChildren();

        if (children.size() == 0) {
            config.setValue(perms.getValue());
            return;
        }

        if (!perms.getValue()) {
            config.getNode(".").setValue(false);
        }

        for (TreeNode child : children) {
            permissionTreeToConfig(config.getNode(child.getKey()), child);
        }
    }

    /**
     * Checks if the specified permissions tree permits the specified permission
     * @param perms The permissions tree
     * @param reqPerms The requested permission to check
     * @return True if the permission tree permits the specified permission, false otherwise
     */
    public static boolean permits(TreeNode perms, String[] reqPerms) {
        return permits(perms, Arrays.asList(reqPerms));
    }

    /**
     * Checks if the specified permissions tree permits the specified permission
     * @param perms The permissions tree
     * @param reqPerms The requested permission to check
     * @return True if the permission tree permits the specified permission, false otherwise
     */
    public static boolean permits(TreeNode perms, List<String> reqPerms) {
        return  subPermissions(perms, reqPerms).getValue();
    }

    /**
     * Gets the permissions tree representing the allowed actions when following the path along the permissions tree
     * @param perms The permissions tree
     * @param path The permission path to follow in the permissions tree
     * @return The sub tree representing the permission permitted for that path
     */
    public static TreeNode subPermissions(TreeNode perms, String[] path) {
        return subPermissions(perms, Arrays.asList(path));
    }

    /**
     * Gets the permissions tree representing the allowed actions when following the path along the permissions tree
     * @param perms The permissions tree
     * @param path The permission path to follow in the permissions tree
     * @return The sub tree representing the permission permitted for that path
     */
    public static TreeNode subPermissions(TreeNode perms, List<String> path) {
        // If we don't have any permissions return an empty node
        if (perms == null) {
            return SecurityService.emptyNode();
        }

        // Check if we ourselves already are a permit-all permission
        if (perms.getKey() != null && perms.getKey().equalsIgnoreCase("*") && perms.getValue()) {
            return perms;
        }

        // Go through all levels of the required permission
        for (String perm : path) {
            // Get the specific permission node for this level, if we have one
            Optional<TreeNode> subPerm = perms.getChild(perm);
            if (subPerm.isPresent()) {
                perms = subPerm.get();
                continue;
            }

            // If we don't have a specific permission for this level, check if there is a "*" permission
            Optional<TreeNode> allChild = perms.getChild("*");
            return allChild.orElseGet(SecurityService::emptyNode);
        }

        // If we get here then that means we have an exact permission for this path
        // or the path had no elements
        return perms;
    }

    /**
     * Returns an empty node, i.e. a node that has no permissions.
     * @return An empty permissions node
     */
    public static TreeNode emptyNode() {
        return new TreeNode(false);
    }

    /**
     * Returns a grant-all permissions node, i.e. a node that contains all permissions.
     * @return A grant-all permissions node
     */
    public static TreeNode permitAllNode() {
        TreeNode node = new TreeNode(true);
        node.addChild(new TreeNode("*", true));
        return node;
    }
}
