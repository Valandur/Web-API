package valandur.webapi.link.ws;

import org.eclipse.jetty.websocket.client.WebSocketClient;
import valandur.webapi.link.LinkClient;
import valandur.webapi.link.message.ConnectMessage;
import valandur.webapi.link.message.DisconnectMessage;
import valandur.webapi.link.message.ResponseMessage;

import java.net.URI;

public class WSClient extends LinkClient {

    private WebSocketClient client;
    private WSClientSocket socket;


    public WSClient(String privateKey) {
        super(privateKey);
        client = new WebSocketClient();
    }

    @Override
    public void connect(String target) {
        try {
            socket = new WSClientSocket(this);

            client.start();
            client.connect(socket, new URI(target));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        if (client == null) {
            return;
        }

        socket.send(new DisconnectMessage(privateKey));

        try {
            client.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void sendConnect(ConnectMessage message) {
        socket.send(message);
    }

    @Override
    protected void sendDisconnect(DisconnectMessage message) {
        socket.send(message);
    }

    @Override
    protected void sendResponse(ResponseMessage message) {
        socket.send(message);
    }
}
