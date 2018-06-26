package valandur.webapi.ipcomm;

import java.util.Map;

public class IPResponse {

    private String id;
    public String getId() {
        return id;
    }

    private int status;
    public int getStatus() {
        return this.status;
    }

    private Map<String, String> headers;
    public Map<String, String> getHeaders() {
        return headers;
    }

    private String message;
    public String getMessage() {
        return message;
    }


    public IPResponse(String id, int status, Map<String, String> headers, String message) {
        this.id = id;
        this.status = status;
        this.headers = headers;
        this.message = message;
    }
}
