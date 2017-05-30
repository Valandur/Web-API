package valandur.webapi.hook;

import valandur.webapi.misc.TreeNode;
import valandur.webapi.permission.Permissions;

import java.util.List;

public class WebHook {

    public enum WebHookMethod {
        GET, PUT, POST, DELETE
    }

    public enum WebHookDataType {
        JSON, FORM
    }

    private String address;
    public String getAddress() {
        return address;
    }

    private boolean enabled;
    public boolean isEnabled() {
        return enabled;
    }


    private WebHookMethod method;
    public WebHookMethod getMethod() { return method; }

    private WebHookDataType dataType;
    public WebHookDataType getDataType() {
        return dataType;
    }
    public String getDataTypeHeader() {
        return "application/" + (dataType == WebHookDataType.JSON ? "json" : "x-www-form-urlencoded");
    }

    private List<WebHookHeader> headers;
    public List<WebHookHeader> getHeaders() { return headers; }

    private boolean details;
    public boolean includeDetails() {
        return details;
    }

    private TreeNode<String, Boolean> permissions = Permissions.emptyNode();
    public TreeNode<String, Boolean> getPermissions() {
        return permissions;
    }

    private WebHookFilter filter;
    public WebHookFilter getFilter() {
        return filter;
    }
    public void setFilter(WebHookFilter filter) {
        this.filter = filter;
    }


    public WebHook(String address, boolean enabled, WebHookMethod method, WebHookDataType dataType,
                   List<WebHookHeader> headers, boolean details, TreeNode<String, Boolean> permissions) {
        this.address = address;
        this.enabled = enabled;
        this.method = method;
        this.dataType = dataType;
        this.headers = headers;
        this.details = details;
        this.permissions = permissions;
    }
}
