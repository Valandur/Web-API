package valandur.webapi.user;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import valandur.webapi.WebAPI;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.config.UserConfig;
import valandur.webapi.security.AuthenticationProvider;
import valandur.webapi.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Users {
    private static final String configFileName = "user.conf";

    private static UserConfig config;
    private static Map<String, UserPermissionStruct> users = new ConcurrentHashMap<>();
    public static List<UserPermissionStruct> getUsers() {
        return new ArrayList<>(users.values());
    }

    public static void init() {
        Logger logger = WebAPI.getLogger();

        logger.info("Loading users...");
        config = Util.loadConfig(configFileName, new UserConfig());

        users = config.users;
    }
    public static void save() {
        config.save();
    }

    public static Optional<UserPermissionStruct> getUser(String username) {
        UserPermissionStruct user = users.get(username);
        return user != null ? Optional.of(user) : Optional.empty();
    }
    public static Optional<UserPermissionStruct> getUser(String username, String password) {
        if (username == null || password == null || !users.containsKey(username)) {
            return Optional.empty();
        }

        try {
            UserPermissionStruct perm = users.get(username);
            if (!BCrypt.checkpw(password, perm.getPassword())) {
                return Optional.empty();
            }

            return Optional.of(perm);
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    public static Optional<UserPermissionStruct> addUser(String username, String password, TreeNode permissions) {
        if (users.containsKey(username))
            return Optional.empty();

        UserPermissionStruct user = new UserPermissionStruct(username, hashPassword(password), permissions);
        users.put(username, user);
        save();

        return Optional.of(user);
    }
    public static UserPermissionStruct modifyUser(UserPermissionStruct user, TreeNode permissions) {
        UserPermissionStruct newUser = user.withPermissions(permissions);
        users.put(newUser.getName(), newUser);
        save();

        AuthenticationProvider.updateAllFrom(newUser);

        return newUser;
    }
    public static Optional<UserPermissionStruct> removeUser(String username) {
        if (!users.containsKey(username))
            return Optional.empty();

        UserPermissionStruct user = users.remove(username);
        save();

        AuthenticationProvider.removeAllFrom(user.getName());

        return Optional.of(user);
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
