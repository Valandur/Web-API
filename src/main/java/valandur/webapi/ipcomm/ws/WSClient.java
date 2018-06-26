package valandur.webapi.ipcomm.ws;

import org.eclipse.jetty.websocket.client.WebSocketClient;
import valandur.webapi.ipcomm.IPClient;

import java.net.URI;

public class WSClient implements IPClient {

    private WebSocketClient client;


    public WSClient() {
        client = new WebSocketClient();
    }

    @Override
    public void connect(String target) {
        try {
            client.start();
            client.connect(new WSClientSocket(), new URI(target));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
