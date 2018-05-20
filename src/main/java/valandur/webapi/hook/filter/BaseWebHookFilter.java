package valandur.webapi.hook.filter;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import valandur.webapi.hook.WebHook;

public abstract class BaseWebHookFilter {

    protected WebHook hook;

    public abstract String getName();


    public BaseWebHookFilter(WebHook hook, ConfigurationNode config) {
        this.hook = hook;
    }

    public void writeToConfig(ConfigurationNode node) throws ObjectMappingException {
        node.getNode("name").setValue(this.getName());
        _writeToConfig(node);
    }
    protected abstract void _writeToConfig(ConfigurationNode node) throws ObjectMappingException;

    public abstract boolean process(Object data);
}
