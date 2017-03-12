package valandur.webapi.hooks;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class WebHook {

    public enum WebHookMethod {
        GET, PUT, POST, DELETE
    }

    @Setting(comment = "The name of the hook (used when referring to it in commands)")
    private String name;
    public String getName() {
        return name != null ? name : address;
    }

    @Setting(comment = "The address of the endpoint that is contacted. This may contain arguments")
    private String address;
    public String getAddress() {
        return address;
    }

    @Setting(comment = "True if this endpoint is called, false otherwise.")
    private boolean enabled = true;
    public boolean isEnabled() {
        return enabled;
    }

    @Setting(comment = "The method which is used when notifying this hook (GET/PUT/POST/DELETE)")
    private WebHookMethod method = WebHookMethod.GET;
    public WebHookMethod getMethod() { return method; }

    @Setting(comment = "The headers that are sent along with the request. You can use this for example to pass along a secret key to ensure the requests are coming from the Web-API")
    private List<WebHookHeader> headers = new ArrayList<>();
    public List<WebHookHeader> getHeaders() { return headers; }

    @Setting(comment = "The parameters can be used in either the header and/or the address, and will also be included in the body")
    private List<WebHookParam> params = new ArrayList<>();
    public List<WebHookParam> getParams() {
        return params;
    }


    public WebHook() {}
}
