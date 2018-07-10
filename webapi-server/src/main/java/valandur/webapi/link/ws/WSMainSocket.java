package valandur.webapi.link.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import valandur.webapi.link.message.BaseMessage;
import valandur.webapi.link.message.ConnectMessage;
import valandur.webapi.link.message.ResponseMessage;

import java.io.IOException;

@WebSocket(maxTextMessageSize = 64 * 1024, maxIdleTime = 24 * 60 * 60 * 1000)
public class WSMainSocket {

    private WSLinkServer link;
    private Session session;
    private String privateKey;


    public WSMainSocket() {
        this.link = WSLinkServer.instance;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        if (privateKey != null) {
            link.removeServer(privateKey);
        }
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            BaseMessage msg = mapper.readValue(message, BaseMessage.class);
            switch (msg.getType()) {
                case CONNECT:
                    ConnectMessage connMsg = (ConnectMessage)msg;
                    this.privateKey = connMsg.getKey();
                    link.addServer(connMsg.getKey(), this);
                    break;

                case DISCONNECT:
                    link.removeServer(privateKey);
                    break;

                case RESPONSE:
                    link.respond((ResponseMessage) msg);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void sendMessage(String message) throws IOException {
        if (privateKey != null) {
            session.getRemote().sendString(message);
        } else {
            throw new IOException("Remote server is not authenticated yet!");
        }
    }
}
