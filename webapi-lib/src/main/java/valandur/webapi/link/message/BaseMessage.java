package valandur.webapi.link.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import valandur.webapi.util.Util;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value=ConnectMessage.class, name = "CONNECT"),
        @JsonSubTypes.Type(value=DisconnectMessage.class, name = "DISCONNECT"),
        @JsonSubTypes.Type(value=RequestMessage.class, name = "REQUEST"),
        @JsonSubTypes.Type(value=ResponseMessage.class, name = "RESPONSE")
})
public abstract class BaseMessage {

    public abstract MessageType getType();

    protected String id;
    public String getId() {
        return id;
    }


    public BaseMessage() {
        this.id = Util.generateUniqueId();
    }
    public BaseMessage(String id) {
        this.id = id;
    }
}
