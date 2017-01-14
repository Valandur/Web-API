package valandur.webapi.servlets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import valandur.webapi.misc.Util;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServletData {
    private HttpServletRequest req;
    private HttpServletResponse resp;

    public PrintWriter getWriter() throws IOException {
        return resp.getWriter();
    }

    private JsonObject json;
    public JsonObject getJson() {
        return json;
    }

    public ServletData(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
        this.json = new JsonObject();
    }

    public Object getAttribute(String name) {
        return req.getAttribute(name);
    }

    public JsonObject addJson(String key, JsonArray arr) {
        this.json.add(key, arr);
        return json;
    }

    public String[] getPathParts() {
        return Util.getPathParts(req);
    }

    public void setStatus(int status) {
        resp.setStatus(status);
    }
    public void sendError(int error) {
        try {
            resp.sendError(error);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
