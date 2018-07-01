package valandur.webapi.ipcomm.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import valandur.webapi.ipcomm.IPLink;
import valandur.webapi.ipcomm.IPRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WSLink extends IPLink {

    public static WSLink instance;
    private Map<String, WSMainSocket> sockets = new ConcurrentHashMap<>();


    @Override
    public void init(ContextHandlerCollection handlers) {
        instance = this;

        ServletContextHandler servletHandler = new ServletContextHandler();
        servletHandler.setContextPath("/");
        servletHandler.addServlet(WSServlet.class, "/ws");
        handlers.addHandler(servletHandler);
    }

    @Override
    public boolean hasServer(String server) {
        return sockets.get(server) != null;
    }

    @Override
    public void send(String server, IPRequest message) {
        WSMainSocket socket = sockets.get(server);
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

    public void connectSocket(String addr, WSMainSocket socket) {
        sockets.put(addr, socket);
    }

    public void disconnectSocket(String addr) {
        sockets.remove(addr);
    }
}
