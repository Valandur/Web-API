package valandur.webapi.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.TreeNode;
import valandur.webapi.misc.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

public class ServletData {
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private boolean errorSent = false;
    private String[] pathParts;
    private Map<String, String> queryParts;

    private TreeNode<String, Boolean> permissions;
    public TreeNode<String, Boolean> getPermissions() {
        return permissions;
    }

    public PrintWriter getWriter() throws IOException {
        return resp.getWriter();
    }

    private ObjectNode node;
    public ObjectNode getNode() {
        return node;
    }

    public boolean isErrorSent() {
        return errorSent;
    }


    public ServletData(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
        this.node = JsonNodeFactory.instance.objectNode();

        this.pathParts = Util.getPathParts(req);
        this.queryParts = Util.getQueryParts(req);
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
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void addJson(String key, Object value, boolean details) {
        node.replace(key, JsonConverter.toJson(value, details, permissions));
    }

    public String[] getPathParts() {
        return pathParts;
    }

    public String getQueryPart(String key) {
        return queryParts.get(key);
    }

    public void sendError(int error, String message) {
        if (errorSent) return;

        try {
            resp.sendError(error, message);
            errorSent = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
