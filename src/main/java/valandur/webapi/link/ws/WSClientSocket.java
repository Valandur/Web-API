package valandur.webapi.link.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import valandur.webapi.WebAPI;
import valandur.webapi.link.message.BaseMessage;
import valandur.webapi.link.message.ConnectMessage;
import valandur.webapi.link.message.RequestMessage;
import valandur.webapi.link.message.ResponseMessage;

import java.io.IOException;

@WebSocket(maxTextMessageSize = 64 * 1024, maxIdleTime = 24 * 60 * 60 * 1000)
public class WSClientSocket {

    private WSClient client;
    private Session session;


    public WSClientSocket(WSClient client) {
        this.client = client;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        send(new ConnectMessage(client.getPrivateKey()));
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        RequestMessage req = parseRequest(msg, RequestMessage.class);
        if (req == null) {
            return;
        }

        ResponseMessage res = WebAPI.emulateRequest(req);
        send(res);
    }

    private <T> T parseRequest(String msg, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(msg, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void send(BaseMessage msg) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            session.getRemote().sendString(mapper.writeValueAsString(msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
