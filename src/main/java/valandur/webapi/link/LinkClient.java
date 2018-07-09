package valandur.webapi.link;

import valandur.webapi.link.message.ConnectMessage;
import valandur.webapi.link.message.DisconnectMessage;
import valandur.webapi.link.message.ResponseMessage;
import valandur.webapi.util.Util;

public abstract class LinkClient {

    protected String id = Util.generateUniqueId();
    public String getId() {
        return id;
    }

    protected String privateKey;
    public String getPrivateKey() {
        return privateKey;
    }


    public abstract void connect(String target);

    public abstract void disconnect();

    protected abstract void sendConnect(ConnectMessage message);
    protected abstract void sendDisconnect(DisconnectMessage message);
    protected abstract void sendResponse(ResponseMessage message);
}
