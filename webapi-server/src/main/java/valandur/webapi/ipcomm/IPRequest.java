package valandur.webapi.ipcomm;

import java.util.HashMap;
import java.util.Map;

public class IPRequest {

    private String id;
    public String getId() {
        return id;
    }

    private String remoteAddr;
    public String getRemoteAddr() {
        return remoteAddr;
    }

    private String method;
    public String getMethod() {
        return method;
    }

    private String path;
    public String getPath() {
        return path;
    }

    private Map<String, String> headers = new HashMap<>();
    public Map<String, String> getHeaders() {
        return headers;
    }

    private Map<String, String> queryParams = new HashMap<>();
    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    private String body;
    public String getBody() {
        return body;
    }


    public IPRequest(String id, String remoteAddr, String method, String path, Map<String, String> headers,
                     Map<String, String> queryParams, String body) {
        this.id = id;
        this.remoteAddr = remoteAddr;
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.queryParams = queryParams;
        this.body = body;
    }
}
