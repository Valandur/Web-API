package valandur.webapi.servlet.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.eclipse.jetty.http.HttpMethod;
import valandur.webapi.WebAPI;
import valandur.webapi.api.permission.IPermissionService;
import valandur.webapi.api.servlet.IServletData;
import valandur.webapi.api.util.TreeNode;
import valandur.webapi.user.UserPermission;
import valandur.webapi.util.Util;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class ServletData implements IServletData {

    private HttpServletRequest req;
    private HttpServletResponse resp;
    private boolean xml = false;
    private boolean errorSent = false;
    private Map<String, String> pathParams;
    private Map<String, String> queryParams;

    private boolean isDone;
    public boolean isDone() {
        return isDone;
    }

    private TreeNode<String, Boolean> permissions;
    public TreeNode<String, Boolean> getPermissions() {
        return permissions;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return resp.getOutputStream();
    }

    private ObjectNode node;
    @Override
    public ObjectNode getNode() {
        return node;
    }

    private Exception lastParseError;
    @Override
    public Exception getLastParseError() {
        return lastParseError;
    }

    @Override
    public boolean isErrorSent() {
        return errorSent;
    }

    @Override
    public boolean responseIsXml() {
        return xml;
    }

    public ServletData(HttpServletRequest req, HttpServletResponse resp, Map<String, String> pathParams) {
        this.req = req;
        this.resp = resp;
        this.node = JsonNodeFactory.instance.objectNode();

        if (req.getHeader("accept") != null && req.getHeader("accept").contains("application/xml")) {
            xml = true;
        }

        this.pathParams = pathParams;
        this.queryParams = Util.getQueryParams(req);
        this.permissions = (TreeNode<String, Boolean>)req.getAttribute("dataPerms");
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.fromString(req.getMethod());
    }

    public String getResponseContentType() {
        if (!xml) {
            return "application/json; charset=utf-8";
        } else {
            return "application/xml; charset=utf-8";
        }
    }
    public void writeResponse() throws IOException {
        ObjectMapper om = xml ? new XmlMapper() : new ObjectMapper();
        resp.getWriter().write(om.writer().withRootName("response").writeValueAsString(node));
    }

    @Override
    public JsonNode getRequestBody() {
        return (JsonNode)req.getAttribute("body");
    }
    @Override
    public <T> Optional<T> getRequestBody(Class<T> clazz) {
        JsonNode json = getRequestBody();
        if (json == null)
            return Optional.empty();

        try {
            T data = WebAPI.getSerializeService().deserialize(json, clazz, IPermissionService.permitAllNode());
            return Optional.of(data);
        } catch (IOException e) {
            lastParseError = e;
            return Optional.empty();
        }
    }

    public UserPermission getUser() {
        return (UserPermission)req.getAttribute("user");
    }

    @Override
    public void setHeader(String name, String value) {
        resp.setHeader(name, value);
    }
    @Override
    public void setStatus(int status) {
        resp.setStatus(status);
    }
    @Override
    public void setContentType(String contentType) {
        resp.setContentType(contentType);
    }
    @Override
    public void addData(String key, Object value, boolean details) {
        node.replace(key, WebAPI.getSerializeService().serialize(value, xml, details, permissions));
    }

    @Override
    public String getPathParam(String key) {
        return pathParams.get(key);
    }
    @Override
    public Optional<String> getQueryParam(String key) {
        String value = queryParams.get(key);
        return value != null ? Optional.of(value) : Optional.empty();
    }

    @Override
    public void setDone() {
        isDone = true;
    }
    @Override
    public void sendError(int error, String message) {
        if (errorSent) return;

        try {
            resp.sendError(error, message);
            errorSent = true;
        } catch (IOException e) {
            e.printStackTrace();
            if (WebAPI.reportErrors()) WebAPI.sentryCapture(e);
        }
    }
}
