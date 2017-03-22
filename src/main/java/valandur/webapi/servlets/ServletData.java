package valandur.webapi.servlets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import valandur.webapi.json.JsonConverter;
import valandur.webapi.misc.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServletData {
    private HttpServletRequest req;
    private HttpServletResponse resp;
    private boolean errorSent = false;

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
    }

    public Object getAttribute(String name) {
        return req.getAttribute(name);
    }

    public void addJson(String key, Object value) {
        if (value instanceof JsonNode) {
            node.replace(key, (JsonNode)value);
        } else {
            node.set(key, JsonConverter.toJson(value, true));
        }
    }

    public String[] getPathParts() {
        return Util.getPathParts(req);
    }

    public void setStatus(int status) {
        resp.setStatus(status);
    }
    public void sendError(int error) {
        if (errorSent) return;

        try {
            resp.sendError(error);
            errorSent = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
