package valandur.webapi.api.hook;

import ninja.leaping.configurate.ConfigurationNode;


public abstract class WebAPIBaseFilter {

    protected IWebHook hook;


    public WebAPIBaseFilter(IWebHook hook, ConfigurationNode config) {
        this.hook = hook;
    }

    public abstract boolean process(Object data);
}
