package valandur.webapi.ipcomm.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import valandur.webapi.ipcomm.IPLink;
import valandur.webapi.ipcomm.IPRequest;
import valandur.webapi.ipcomm.IPResponse;

import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class WSLink extends IPLink {

    private Map<String, WSMainSocket> sockets = new ConcurrentHashMap<>();


    @Override
    public void init() {
    }

    @Override
    public Class<WSServlet> getServletClass() {
        return WSServlet.class;
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
