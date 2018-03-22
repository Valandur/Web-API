package valandur.webapi.hook;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import valandur.webapi.WebAPI;
import valandur.webapi.api.hook.BaseWebHookFilter;
import valandur.webapi.api.hook.IWebHook;
import valandur.webapi.api.hook.WebHookHeader;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.util.TreeNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WebHookSerializer implements TypeSerializer<WebHook> {

    @Override
    public WebHook deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        Logger logger = WebAPI.getLogger();

        String address = value.getNode("address").getString();

        if (address == null) {
            logger.error("No address specified for web hook!");
            return null;
        }

        logger.info("  - " + address);

        boolean enabled = value.getNode("enabled").getBoolean();
        HttpMethod method = value.getNode("method").getValue(TypeToken.of(HttpMethod.class));
        WebHook.WebHookDataType dataType = value.getNode("dataType").getValue(TypeToken.of(WebHook.WebHookDataType.class));
        boolean form = value.getNode("form").getBoolean();
        List<WebHookHeader> headers = value.getNode("headers").getList(TypeToken.of(WebHookHeader.class));
        boolean details = value.getNode("details").getBoolean();

        ConfigurationNode filterBase = value.getNode("filter");
        String filterName = filterBase.getNode("name").getString();
        ConfigurationNode filterConfig = filterBase.getNode("config");

        TreeNode<String, Boolean> permissions = IPermissionService.permitAllNode();

        if (headers == null) {
            headers = new ArrayList<>();
        }

        if (method == null) {
            method = HttpMethod.POST;
            logger.warn("    Does not specify 'method', defaulting to 'POST'");
        }

        if (dataType == null) {
            dataType = WebHook.WebHookDataType.JSON;
            logger.warn("    Does not specify 'dataType', defaulting to 'JSON'");
        }

        if (value.getNode("permissions").isVirtual()) {
            logger.warn("    Does not specify 'permissions', defaulting to '*'");
        } else {
            permissions = WebAPI.getPermissionService().permissionTreeFromConfig(value.getNode("permissions"));
        }

        WebHook hook = new WebHook(address, enabled, method, dataType, form, headers, details, permissions);

        if (filterName != null) {
            Optional<Class<? extends BaseWebHookFilter>> opt = WebAPI.getWebHookService().getFilter(filterName);
            if (!opt.isPresent()) {
                logger.error("    Could not find filter with name '" + filterName + "'");
            } else {
                try {
                    Constructor ctor = opt.get().getConstructor(WebHook.class, ConfigurationNode.class);
                    hook.setFilter((BaseWebHookFilter) ctor.newInstance(hook, filterConfig));
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    logger.error("    Could not setup filter '" + filterName + "': " + e.getMessage());
                }
            }
        }

        if (enabled) {
            logger.info("    -> Ok");
        } else {
            logger.info("    -> Disabled");
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

        setValueAndComment(value.getNode("method"), TypeToken.of(HttpMethod.class), obj.getMethod(),
                "This is the http method that is used (GET, PUT, POST or DELETE)");

        setValueAndComment(value.getNode("dataType"), TypeToken.of(IWebHook.WebHookDataType.class), obj.getDataType(),
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

        WebAPI.getPermissionService().permissionTreeToConfig(value.getNode("permissions"), obj.getPermissions());
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
