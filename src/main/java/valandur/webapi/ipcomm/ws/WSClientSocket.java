package valandur.webapi.ipcomm.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import valandur.webapi.WebAPI;
import valandur.webapi.ipcomm.IPRequest;
import valandur.webapi.ipcomm.IPResponse;

import javax.servlet.ServletException;
import java.io.IOException;

@WebSocket(maxTextMessageSize = 64 * 1024, maxIdleTime = 24 * 60 * 60 * 1000)
public class WSClientSocket {

    private Session session;


    public WSClientSocket() { }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        System.out.println("Connected to main server");
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Disconnected from main server");
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        System.out.printf("Got msg: %s%n",msg);

        ObjectMapper mapper = new ObjectMapper();
        try {
            IPRequest message = mapper.readValue(msg, IPRequest.class);
            IPResponse res = WebAPI.emulateRequest(message);
            session.getRemote().sendString(mapper.writeValueAsString(res));
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }
    }
}
