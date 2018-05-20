package valandur.webapi.hook;

import valandur.webapi.hook.filter.BaseWebHookFilter;
import valandur.webapi.util.TreeNode;

import java.util.List;

public class WebHook {

    public enum WebHookDataType {
        JSON, XML
    }

    private String address;
    public String getAddress() {
        return address;
    }

    private boolean enabled;
    public boolean isEnabled() {
        return enabled;
    }

    private boolean form;
    public boolean isForm() {
        return form;
    }

    private String method;
    public String getMethod() { return method; }

    private WebHookDataType dataType;
    public WebHookDataType getDataType() {
        return dataType;
    }
    public String getDataTypeHeader() {
        if (form)
            return "application/x-www-form-urlencoded";

        switch (dataType) {
            case JSON:
                return "application/json";
            case XML:
                return "application/xml";
            default:
                return "";
        }
    }

    private List<WebHookHeader> headers;
    public List<WebHookHeader> getHeaders() { return headers; }

    private boolean details;
    public boolean includeDetails() {
        return details;
    }

    private TreeNode permissions;
    public TreeNode getPermissions() {
        return permissions;
    }

    private BaseWebHookFilter filter;
    public BaseWebHookFilter getFilter() {
        return filter;
    }
    public void setFilter(BaseWebHookFilter filter) {
        this.filter = filter;
    }


    public WebHook(String address, boolean enabled, String method, WebHookDataType dataType, boolean form,
                   List<WebHookHeader> headers, boolean details, TreeNode permissions) {
        this.address = address;
        this.enabled = enabled;
        this.method = method;
        this.dataType = dataType;
        this.form = form;
        this.headers = headers;
        this.details = details;
        this.permissions = permissions;
    }
}
