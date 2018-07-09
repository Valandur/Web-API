package valandur.webapi.link.message;

public class DisconnectMessage extends BaseMessage {

    @Override
    public MessageType getType() {
        return MessageType.DISCONNECT;
    }

    private String key;
    public String getKey() {
        return key;
    }


    public DisconnectMessage(String key) {
        this.key = key;
    }
}
