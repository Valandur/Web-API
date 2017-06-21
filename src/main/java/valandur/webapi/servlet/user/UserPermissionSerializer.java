package valandur.webapi.servlet.user;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import valandur.webapi.misc.TreeNode;
import valandur.webapi.permission.Permissions;

public class UserPermissionSerializer implements TypeSerializer<UserPermission> {
    @Override
    public UserPermission deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String username = value.getNode("username").getString();
        String password = value.getNode("password").getString();

        TreeNode<String, Boolean> perms = Permissions.permissionTreeFromConfig(value.getNode("permissions"));

        return new UserPermission(username, password, perms);
    }

    @Override
    public void serialize(TypeToken<?> type, UserPermission obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("username").setValue(obj.getUsername());
        value.getNode("password").setValue(obj.getPassword());
        Permissions.permissionTreeToConfig(value.getNode("permissions"), obj.getPermissions());
    }
}
