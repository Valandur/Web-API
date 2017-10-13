package valandur.webapi.api.hook;

import ninja.leaping.configurate.ConfigurationNode;


public abstract class BaseWebHookFilter {

    protected IWebHook hook;


    public BaseWebHookFilter(IWebHook hook, ConfigurationNode config) {
        this.hook = hook;
    }

    public abstract boolean process(Object data);
}
