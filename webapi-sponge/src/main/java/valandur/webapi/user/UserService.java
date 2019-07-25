package valandur.webapi.user;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import valandur.webapi.WebAPI;
import valandur.webapi.config.BaseConfig;
import valandur.webapi.config.UserConfig;
import valandur.webapi.util.TreeNode;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private static final String configFileName = "user.conf";

    private UserConfig config;
    private Map<String, UserPermissionStruct> users = new ConcurrentHashMap<>();
    public List<UserPermissionStruct> getUsers() {
        return new ArrayList<>(users.values());
    }

    public void init() {
        Logger logger = WebAPI.getInstance().getLogger();

        logger.info("Loading users...");
        Path configPath = WebAPI.getConfigPath().resolve(configFileName).normalize();
        config = BaseConfig.load(configPath, new UserConfig());

        users = config.users;
    }
    public void save() {
        config.save();
    }

    public Optional<UserPermissionStruct> getUser(String username) {
        UserPermissionStruct user = users.get(username);
        return user != null ? Optional.of(user) : Optional.empty();
    }
    public Optional<UserPermissionStruct> getUser(String username, String password) {
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

    public Optional<UserPermissionStruct> addUser(String username, String password, TreeNode permissions) {
        if (users.containsKey(username))
            return Optional.empty();

        UserPermissionStruct user = new UserPermissionStruct(username, hashPassword(password), permissions);
        users.put(username, user);
        save();

        return Optional.of(user);
    }
    public UserPermissionStruct modifyUser(UserPermissionStruct user, TreeNode permissions) {
        UserPermissionStruct newUser = user.withPermissions(permissions);
        users.put(newUser.getName(), newUser);
        save();

        WebAPI.getSecurityService().updateAllFrom(newUser);

        return newUser;
    }
    public Optional<UserPermissionStruct> removeUser(String username) {
        if (!users.containsKey(username))
            return Optional.empty();

        UserPermissionStruct user = users.remove(username);
        save();

        WebAPI.getSecurityService().removeAllFrom(user.getName());

        return Optional.of(user);
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
