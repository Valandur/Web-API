package valandur.webapi.hook;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import valandur.webapi.WebAPI;
import valandur.webapi.hook.filter.BaseWebHookFilter;
import valandur.webapi.security.SecurityService;
import valandur.webapi.util.TreeNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WebHookSerializer implements TypeSerializer<WebHook> {
    @Override
    public WebHook deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        String address = value.getNode("address").getString();

        if (address == null) {
            throw new ObjectMappingException("No address specified for web hook!");
        }

        boolean enabled = value.getNode("enabled").getBoolean();
        String method = value.getNode("method").getString();
        WebHook.WebHookDataType dataType = value.getNode("dataType").getValue(TypeToken.of(WebHook.WebHookDataType.class));
        boolean form = value.getNode("form").getBoolean();
        List<WebHookHeader> headers = value.getNode("headers").getList(TypeToken.of(WebHookHeader.class));
        boolean details = value.getNode("details").getBoolean();

        ConfigurationNode filterBase = value.getNode("filter");
        String filterName = filterBase.getNode("name").getString();
        ConfigurationNode filterConfig = filterBase.getNode("config");

        TreeNode permissions;

        if (headers == null) {
            headers = new ArrayList<>();
        }

        if (method == null) {
            throw new ObjectMappingException("Webhook " + address + " is missing property 'method'.");
        }

        if (dataType == null) {
            throw new ObjectMappingException("Webhook " + address + " is missing property 'dataType'.");
        }

        if (value.getNode("permissions").isVirtual()) {
            throw new ObjectMappingException("Webhook " + address + " is missing property 'permissions'.");
        } else {
            permissions = SecurityService.permissionTreeFromConfig(value.getNode("permissions"));
        }

        WebHook hook = new WebHook(address, enabled, method, dataType, form, headers, details, permissions);

        if (filterName != null) {
            Optional<Class<? extends BaseWebHookFilter>> opt = WebAPI.getWebHookService().getFilter(filterName);
            if (!opt.isPresent()) {
                throw new ObjectMappingException("Could not find filter with name '" + filterName + "'");
            } else {
                try {
                    Constructor ctor = opt.get().getConstructor(WebHook.class, ConfigurationNode.class);
                    hook.setFilter((BaseWebHookFilter) ctor.newInstance(hook, filterConfig));
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new ObjectMappingException("Could not setup filter '" + filterName + "': " + e.getMessage());
                }
            }
        }

        return hook;
    }

    @Override
    public void serialize(TypeToken<?> type, WebHook obj, ConfigurationNode value) throws ObjectMappingException {
        setValueAndComment(value.getNode("address"), obj.getAddress(),
                "This is the address of the endpoint.");

        setValueAndComment(value.getNode("enabled"), obj.isEnabled(),
                "Set to true or omit to enable the endpoint.");

        setValueAndComment(value.getNode("headers"), new TypeToken<List<WebHookHeader>>() {}, obj.getHeaders(),
                "This is a list of additional headers that is sent to the server. You can use this to e.g. specify a secret\n" +
                        "key which ensures that the server knows the requests are coming from the Web-API.\n" +
                        "Please note that the following headers will always be overridden by the Web-API:\n" +
                        "X-WebAPI-Version, X-WebAPI-Event, X-WebAPI-Source, User-Agent, Content-Type, Content-Length, accept, charset");

        setValueAndComment(value.getNode("method"), obj.getMethod(),
                "This is the http method that is used (GET, PUT, POST or DELETE)");

        setValueAndComment(value.getNode("dataType"), TypeToken.of(WebHook.WebHookDataType.class), obj.getDataType(),
                "Choose to either send the data as:\n" +
                        "JSON = application/json\n" +
                        "XML = application/xml");

        setValueAndComment(value.getNode("form"), obj.isForm(),
                "Choose to send the data wrapped as application/x-www-form-urlencoded");

        setValueAndComment(value.getNode("details"), obj.includeDetails(),
                "Set to true to send detailed json data");

        if (obj.getFilter() != null) {
            ConfigurationNode filterNode = value.getNode("filter");
            obj.getFilter().writeToConfig(filterNode);
            if (filterNode instanceof CommentedConfigurationNode) {
                ((CommentedConfigurationNode) filterNode).setComment(
                        "Filters are used to only send certain events to certain endpoints\n" +
                                "Use the 'name' property to select a filter and pass additional options in the 'config' property"
                );
            }
        }

        WebAPI.getSecurityService().permissionTreeToConfig(value.getNode("permissions"), obj.getPermissions());
        if (value.getNode("permissions") instanceof CommentedConfigurationNode) {
            ((CommentedConfigurationNode) value.getNode("permissions")).setComment(
                    "Permissions node same as the ones in the permissions.conf file,\n" +
                            "use to configure which data is sent to this node");
        }
    }

    private void setValueAndComment(ConfigurationNode node, Object value, String comment) {
        node.setValue(value);
        if (node instanceof CommentedConfigurationNode) {
            ((CommentedConfigurationNode) node).setComment(comment);
        }
    }
    private <T> void setValueAndComment(ConfigurationNode node, TypeToken<T> type, T value, String comment)
            throws ObjectMappingException {
        node.setValue(type, value);
        if (node instanceof CommentedConfigurationNode) {
            ((CommentedConfigurationNode) node).setComment(comment);
        }
    }
}
