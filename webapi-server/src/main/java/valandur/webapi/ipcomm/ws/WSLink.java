package valandur.webapi.ipcomm.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import valandur.webapi.ipcomm.IPLink;
import valandur.webapi.ipcomm.IPRequest;
import valandur.webapi.ipcomm.IPResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class WSLink implements IPLink {

    private static Map<String, WSMainSocket> sockets = new ConcurrentHashMap<>();
    private static List<Function<IPResponse, Void>> listeners = new ArrayList<>();


    @Override
    public void init() {
    }

    @Override
    public void start() {
    }

    @Override
    public boolean hasServer(String server) {
        return sockets.get(server) != null;
    }

    @Override
    public boolean send(String server, IPRequest message) {
        WSMainSocket socket = sockets.get(server);
        if (socket == null) {
            return false;
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            String msg = mapper.writeValueAsString(message);
            socket.sendMessage(msg);
            return true;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onResponse(Function<IPResponse, Void> callback) {
        listeners.add(callback);
    }


    public static void connectSocket(String key, WSMainSocket socket) {
        sockets.put(key, socket);
    }

    public static void disconnectSocket(String key) {
        sockets.remove(key);
    }

    public static void onResponse(IPResponse res) {
        listeners.forEach(c -> c.apply(res));
    }
}
