package valandur.webapi.hook;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.slf4j.Logger;
import valandur.webapi.WebAPI;
import valandur.webapi.misc.TreeNode;
import valandur.webapi.permission.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WebHookSerializer implements TypeSerializer<WebHook> {
    @Override
    public WebHook deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Logger logger = WebAPI.getInstance().getLogger();

        String address = value.getNode("address").getString();

        logger.info("  - " + address);

        boolean enabled = value.getNode("enabled").getBoolean();
        WebHook.WebHookMethod method = value.getNode("method").getValue(TypeToken.of(WebHook.WebHookMethod.class));
        WebHook.WebHookDataType dataType = value.getNode("dataType").getValue(TypeToken.of(WebHook.WebHookDataType.class));
        List<WebHookHeader> headers = value.getNode("headers").getList(TypeToken.of(WebHookHeader.class));
        boolean details = value.getNode("details").getBoolean();
        String filterName = value.getNode("filter").getString();

        WebAPIFilter filter = null;

        TreeNode<String, Boolean> permissions = Permissions.permitAllNode();

        if (!enabled) {
            logger.info("    -> Disabled");
        } else {
            if (headers == null) {
                headers = new ArrayList<>();
            }

            if (method == null) {
                method = WebHook.WebHookMethod.POST;
                logger.warn("   Does not specify 'method', defaulting to 'POST'");
            }

            if (dataType == null) {
                dataType = WebHook.WebHookDataType.JSON;
                logger.warn("   Does not specify 'dataType', defaulting to 'JSON'");
            }

            if (value.getNode("permissions").isVirtual()) {
                logger.warn("   Does not specify 'permissions', defaulting to '*'");
            } else {
                permissions = Permissions.permissionTreeFromConfig(value.getNode("permissions"));
            }

            if (filterName != null) {
                Optional<WebAPIFilter> opt = WebHooks.getFilter(filterName);
                if (!opt.isPresent()) {
                    logger.warn("   Could not find filter with name '" + filterName + "'");
                } else {
                    filter = opt.get();
                }
            }

            logger.info("    -> Ok");
        }

        return new WebHook(address, enabled, method, dataType, headers, details, permissions, filter);
    }

    @Override
    public void serialize(TypeToken<?> type, WebHook obj, ConfigurationNode value) throws ObjectMappingException {
        value.getNode("address").setValue(obj.getAddress());
        value.getNode("enabled").setValue(obj.isEnabled());
        value.getNode("headers").setValue(obj.getHeaders());
        value.getNode("method").setValue(obj.getMethod());
        value.getNode("dataType").setValue(obj.getDataType());
        value.getNode("details").setValue(obj.includeDetails());
        Permissions.permissionTreeToConfig(value.getNode("permissions"), obj.getPermissions());
    }
}
