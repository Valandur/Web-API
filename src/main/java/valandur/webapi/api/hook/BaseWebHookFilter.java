package valandur.webapi.api.hook;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.util.Map;

public abstract class BaseWebHookFilter {

    protected IWebHook hook;

    public abstract String getName();


    public BaseWebHookFilter(IWebHook hook, ConfigurationNode config) {
        this.hook = hook;
    }

    public void writeToConfig(ConfigurationNode node) throws ObjectMappingException {
        node.getNode("name").setValue(this.getName());
        _writeToConfig(node);
    }
    protected abstract void _writeToConfig(ConfigurationNode node) throws ObjectMappingException;

    public abstract boolean process(Object data);
}
