package valandur.webapi.hook;

import valandur.webapi.api.hook.IWebHook;
import valandur.webapi.api.hook.BaseWebHookFilter;
import valandur.webapi.api.hook.WebHookHeader;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.util.TreeNode;

import java.util.List;

public class WebHook implements IWebHook {

    private String address;
    @Override
    public String getAddress() {
        return address;
    }

    private boolean enabled;
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    private boolean form;
    @Override
    public boolean isForm() {
        return form;
    }

    private WebHookMethod method;
    @Override
    public WebHookMethod getMethod() { return method; }

    private WebHookDataType dataType;
    @Override
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
    @Override
    public List<WebHookHeader> getHeaders() { return headers; }

    private boolean details;
    @Override
    public boolean includeDetails() {
        return details;
    }

    private TreeNode<String, Boolean> permissions = IPermissionService.emptyNode();
    @Override
    public TreeNode<String, Boolean> getPermissions() {
        return permissions;
    }

    private BaseWebHookFilter filter;
    @Override
    public BaseWebHookFilter getFilter() {
        return filter;
    }
    public void setFilter(BaseWebHookFilter filter) {
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
