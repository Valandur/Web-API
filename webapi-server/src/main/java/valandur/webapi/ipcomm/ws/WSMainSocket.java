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

    private WSLink link;
    private Session session;
    private String addr;


    public WSMainSocket(WSLink link) {
        this.link = link;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        addr = session.getRemoteAddress().getHostName();
        System.out.println("Connected: " + addr);

        this.session = session;
        link.connectSocket(addr, this);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Disconnected: " + addr);

        link.disconnectSocket(addr);
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        System.out.printf("Got msg: %s%n", msg);

        link.respond(msg);
    }


    public void sendMessage(String message) throws IOException {
        session.getRemote().sendString(message);
    }
}
