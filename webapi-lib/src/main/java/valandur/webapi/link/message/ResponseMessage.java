package valandur.webapi.link.message;

import java.util.Map;

public class ResponseMessage extends BaseMessage {

    @Override
    public MessageType getType() {
        return MessageType.RESPONSE;
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


    public ResponseMessage(String id, int status, Map<String, String> headers, String message) {
        super(id);

        this.status = status;
        this.headers = headers;
        this.message = message;
    }
}
