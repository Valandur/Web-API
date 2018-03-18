package valandur.webapi.api.hook;

import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.api.util.TreeNode;

import java.util.List;

public interface IWebHook {

    enum WebHookDataType {
        JSON, XML
    }


    String getAddress();

    boolean isEnabled();

    HttpMethod getMethod();

    WebHookDataType getDataType();

    boolean isForm();

    String getDataTypeHeader();

    List<WebHookHeader> getHeaders();

    boolean includeDetails();

    TreeNode<String, Boolean> getPermissions();

    BaseWebHookFilter getFilter();

    void setFilter(BaseWebHookFilter filter);
}
