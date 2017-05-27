package valandur.webapi.hook;

public abstract class WebAPIFilter {
    public abstract String getName();
    public abstract boolean process(WebHook hook, Object data);
}
