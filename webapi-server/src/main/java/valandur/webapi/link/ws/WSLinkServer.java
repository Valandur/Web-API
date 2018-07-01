package valandur.webapi.link.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import valandur.webapi.link.LinkServer;
import valandur.webapi.link.message.RequestMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WSLinkServer extends LinkServer {

    public static WSLinkServer instance;
    private Map<String, WSMainSocket> sockets = new ConcurrentHashMap<>();


    public WSLinkServer(Map<String, String> serverKeys) {
        super(serverKeys);
    }

    @Override
    public void init(ContextHandlerCollection handlers) {
        instance = this;

        ServletContextHandler servletHandler = new ServletContextHandler();
        servletHandler.setContextPath("/");
        servletHandler.addServlet(WSServlet.class, "/ws");
        handlers.addHandler(servletHandler);
    }

    @Override
    public void sendRequest(String serverName, RequestMessage message) {
        WSMainSocket socket = sockets.get(serverKeys.get(serverName));
        if (socket == null) {
            return;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            String msg = mapper.writeValueAsString(message);
            socket.sendMessage(msg);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    void addServer(String privateKey, WSMainSocket socket) {
        if (super.addServer(privateKey)) {
            sockets.put(privateKey, socket);
        }
    }
}
