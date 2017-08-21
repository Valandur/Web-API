package valandur.webapi.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.sentry.Sentry;
import valandur.webapi.WebAPI;
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
    public ObjectNode getNode() {
        return node;
    }

    private Exception lastParseError;
    public Exception getLastParseError() {
        return lastParseError;
    }

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

    public JsonNode getRequestBody() {
        return (JsonNode)req.getAttribute("body");
    }
    public <T> Optional<T> getRequestBody(Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            T data = mapper.treeToValue(getRequestBody(), clazz);
            return Optional.of(data);
        } catch (JsonProcessingException e) {
            lastParseError = e;
            return Optional.empty();
        }
    }

    public UserPermission getUser() {
        return (UserPermission)req.getAttribute("user");
    }

    public void setHeader(String name, String value) {
        resp.setHeader(name, value);
    }
    public void setStatus(int status) {
        resp.setStatus(status);
    }
    public void setContentType(String contentType) {
        resp.setContentType(contentType);
    }
    public void addJson(String key, Object value, boolean details) {
        node.replace(key, WebAPI.getJsonService().toJson(value, details, permissions));
    }

    public String getPathParam(String key) {
        return pathParams.get(key);
    }
    public Optional<String> getQueryParam(String key) {
        String value = queryParams.get(key);
        return value != null ? Optional.of(value) : Optional.empty();
    }

    public void setDone() {
        isDone = true;
    }
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
