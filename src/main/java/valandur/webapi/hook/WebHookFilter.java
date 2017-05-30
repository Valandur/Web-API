package valandur.webapi.hook;

import ninja.leaping.configurate.ConfigurationNode;


public abstract class WebHookFilter {

    protected WebHook hook;


    public WebHookFilter(WebHook hook, ConfigurationNode config) {
        this.hook = hook;
    }

    public abstract boolean process(Object data);
}
