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

    public enum WebHookDataType {
        JSON, FORM
    }

    @Setting
    private String name;
    public String getName() {
        return name != null ? name : address;
    }

    @Setting
    private List<String> aliases;
    public List<String> getAliases() {
        return aliases;
    }

    @Setting
    private String address;
    public String getAddress() {
        return address;
    }

    @Setting
    private boolean enabled = true;
    public boolean isEnabled() {
        return enabled;
    }

    @Setting
    private WebHookMethod method = WebHookMethod.GET;
    public WebHookMethod getMethod() { return method; }

    @Setting
    private WebHookDataType dataType = WebHookDataType.JSON;
    public WebHookDataType getDataType() {
        return dataType;
    }
    public String getDataTypeHeader() {
        return "application/" + (dataType == WebHookDataType.JSON ? "json" : "x-www-form-urlencoded");
    }

    @Setting
    private List<WebHookHeader> headers = new ArrayList<>();
    public List<WebHookHeader> getHeaders() { return headers; }

    @Setting
    private List<WebHookParam> params = new ArrayList<>();
    public List<WebHookParam> getParams() {
        return params;
    }


    public WebHook() {}
}
