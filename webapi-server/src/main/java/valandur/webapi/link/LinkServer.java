package valandur.webapi.link;

import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import valandur.webapi.link.message.RequestMessage;
import valandur.webapi.link.message.ResponseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

public abstract class LinkServer {

    private List<Consumer<ResponseMessage>> listeners = new ArrayList<>();

    // Mapping of server name to server key
    protected Map<String, String> serverKeys;
    // Set of connected server keys
    protected Set<String> servers = new ConcurrentSkipListSet<>();


    public LinkServer(Map<String, String> serverKeys) {
        this.serverKeys = serverKeys;
    }

    public abstract void init(ContextHandlerCollection handlers);

    public boolean addServer(String serverKey) {
        if (serverKeys.containsValue(serverKey)) {
            System.out.println("Connected: " + serverKeys.get(serverKey));
            servers.add(serverKey);
            return true;
        }
        System.out.println("Refused connection: " + serverKey);
        return false;
    }
    public boolean hasServer(String serverName) {
        return serverKeys.containsKey(serverName);
    }
    public boolean isConnected(String serverName) {
        return serverKeys.containsKey(serverName) && servers.contains(serverKeys.get(serverName));
    }
    public void removeServer(String serverKey) {
        System.out.println("Disconnected: " + serverKeys.get(serverKey));
        servers.remove(serverKey);
    }

    public abstract void sendRequest(String serverName, RequestMessage message);

    public void respond(ResponseMessage message) {
        listeners.forEach(c -> c.accept(message));
    }
    public void onResponse(Consumer<ResponseMessage> callback) {
        listeners.add(callback);
    }
}
