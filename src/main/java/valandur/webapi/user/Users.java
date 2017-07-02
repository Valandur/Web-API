package valandur.webapi.user;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.spongepowered.api.util.Tuple;
import valandur.webapi.WebAPI;
import valandur.webapi.permission.PermissionService;
import valandur.webapi.util.Util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Users {
    private static final String configFileName = "user.conf";

    private static ConfigurationLoader loader;
    private static ConfigurationNode config;
    private static Map<String, UserPermission> users = new HashMap<>();
    public static Collection<UserPermission> getUsers() {
        return users.values();
    }

    public static void init() {
        Logger logger = WebAPI.getLogger();

        logger.info("Loading users...");
        Tuple<ConfigurationLoader, ConfigurationNode> tup =
                Util.loadWithDefaults(configFileName, "defaults/" + configFileName);
        loader = tup.getFirst();
        config = tup.getSecond();

        users.clear();

        try {
            Map<Object, ? extends ConfigurationNode> nodes = config.getNode("users").getChildrenMap();
            for (Map.Entry<Object, ? extends ConfigurationNode> entry : nodes.entrySet()) {
                UserPermission perm = entry.getValue().getValue(TypeToken.of(UserPermission.class));
                users.put(perm.getUsername(), perm);
            }
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }
    private static void save() {
        ConfigurationNode rootNode = config.getNode("users");
        for (UserPermission perm : users.values()) {
            try {
                rootNode.getNode(perm.getUsername()).setValue(TypeToken.of(UserPermission.class), perm);
            } catch (ObjectMappingException ignored) {
            }
        }
        try {
            loader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Optional<UserPermission> getUser(String username) {
        UserPermission user = users.get(username);
        return user != null ? Optional.of(user) : Optional.empty();
    }
    public static Optional<UserPermission> getUser(String username, String password) {
        if (!users.containsKey(username)) {
            return Optional.empty();
        }

        try {
            UserPermission perm = users.get(username);
            if (!BCrypt.checkpw(password, perm.getPassword())) {
                return Optional.empty();
            }

            return Optional.of(perm);
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    public static boolean addUser(String username, String password) {
        if (users.containsKey(username))
            return false;
        users.put(username, new UserPermission(username, hashPassword(password), PermissionService.permitAllNode()));
        save();
        return true;
    }
}
