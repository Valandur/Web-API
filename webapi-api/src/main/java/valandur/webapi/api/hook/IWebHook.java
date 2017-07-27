package valandur.webapi.api.hook;

import valandur.webapi.api.util.TreeNode;

import java.util.List;

public interface IWebHook {

    enum WebHookMethod {
        GET, PUT, POST, DELETE
    }

    enum WebHookDataType {
        JSON, FORM
    }


    String getAddress();

    boolean isEnabled();

    WebHookMethod getMethod();

    WebHookDataType getDataType();

    String getDataTypeHeader();

    List<WebHookHeader> getHeaders();

    boolean includeDetails();

    TreeNode<String, Boolean> getPermissions();

    WebAPIBaseFilter getFilter();

    void setFilter(WebAPIBaseFilter filter);
}
