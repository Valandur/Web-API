package valandur.webapi.link.message;

public class ConnectMessage extends BaseMessage {

    @Override
    public MessageType getType() {
        return MessageType.CONNECT;
    }

    private String key;
    public String getKey() {
        return key;
    }


    public ConnectMessage() {}
    public ConnectMessage(String key) {
        this.key = key;
    }
}
