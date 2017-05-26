package valandur.webapi.hook;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import valandur.webapi.misc.TreeNode;
import valandur.webapi.permission.Permissions;

import java.util.List;

public class WebHookSerializer implements TypeSerializer<WebHook> {
    @Override
    public WebHook deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String address = value.getNode("address").getString();
        boolean enabled = value.getNode("enabled").getBoolean();
        WebHook.WebHookMethod method = value.getNode("method").getValue(TypeToken.of(WebHook.WebHookMethod.class));
        WebHook.WebHookDataType dataType = value.getNode("dataType").getValue(TypeToken.of(WebHook.WebHookDataType.class));
        List<WebHookHeader> headers = value.getNode("headers").getList(TypeToken.of(WebHookHeader.class));
        boolean details = value.getNode("details").getBoolean();
        TreeNode<String, Boolean> permissions = Permissions.permissionTreeFromConfig(value.getNode("permissions"));

        return new WebHook(address, enabled, method, dataType, headers, details, permissions);
    }

    @Override
    public void serialize(TypeToken<?> type, WebHook obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("address").setValue(obj.getAddress());
        value.getNode("enabled").setValue(obj.isEnabled());
        value.getNode("method").setValue(obj.getMethod());
        value.getNode("dataType").setValue(obj.getDataType());
        value.getNode("details").setValue(obj.includeDetails());
        Permissions.permissionTreeToConfig(value, obj.getPermissions());
    }
}
