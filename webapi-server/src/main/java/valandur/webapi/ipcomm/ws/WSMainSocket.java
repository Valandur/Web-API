package valandur.webapi.ipcomm.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import valandur.webapi.ipcomm.IPResponse;

import javax.xml.ws.soap.Addressing;
import java.io.IOException;
import java.util.concurrent.Future;

@WebSocket(maxTextMessageSize = 64 * 1024, maxIdleTime = 24 * 60 * 60 * 1000)
public class WSMainSocket {

    private Session session;
    private String addr;


    public WSMainSocket() { }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        addr = session.getRemoteAddress().getHostName();
        System.out.println("Connected: " + addr);

        this.session = session;
        WSLink.connectSocket(addr, this);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Disconnected: " + addr);

        WSLink.disconnectSocket(addr);
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        System.out.printf("Got msg: %s%n",msg);

        try {
            ObjectMapper mapper = new ObjectMapper();
            IPResponse res = mapper.readValue(msg, IPResponse.class);
            WSLink.onResponse(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(String message) throws IOException {
        session.getRemote().sendString(message);
    }
}