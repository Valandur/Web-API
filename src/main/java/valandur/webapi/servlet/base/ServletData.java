package valandur.webapi.servlet.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

public class ServletData implements IServletData {

    private HttpServletRequest req;
    private HttpServletResponse resp;
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

    public ServletOutputStream getOutputStream() throws IOException {
        return resp.getOutputStream();
    }
    public PrintWriter getWriter() throws IOException {
        return resp.getWriter();
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


    public ServletData(HttpServletRequest req, HttpServletResponse resp, Map<String, String> pathParams) {
        this.req = req;
        this.resp = resp;
        this.node = JsonNodeFactory.instance.objectNode();

        this.pathParams = pathParams;
        this.queryParams = Util.getQueryParams(req);
        this.permissions = (TreeNode<String, Boolean>)req.getAttribute("dataPerms");
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.fromString(req.getMethod());
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
            T data = WebAPI.getJsonService().toObject(json, clazz, IPermissionService.permitAllNode());
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
    public void addJson(String key, Object value, boolean details) {
        node.replace(key, WebAPI.getJsonService().toJson(value, details, permissions));
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
