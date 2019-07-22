package valandur.webapi.security;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import valandur.webapi.WebAPI;
import valandur.webapi.util.TreeNode;

public class PermissionStructSerializer implements TypeSerializer<PermissionStruct> {

    @Override
    public PermissionStruct deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String key = value.getKey().toString();

        TreeNode perms = WebAPI.getSecurityService().permissionTreeFromConfig(value.getNode("permissions"));
        int rateLimit = value.getNode("rateLimit").getInt();

        return new PermissionStruct(key, perms, rateLimit);
    }

    @Override
    public void serialize(TypeToken<?> type, PermissionStruct obj, ConfigurationNode value) throws ObjectMappingException {
        WebAPI.getSecurityService().permissionTreeToConfig(value.getNode("permissions"), obj.getPermissions());
        value.getNode("rateLimit").setValue(obj.getRateLimit());
    }
}
