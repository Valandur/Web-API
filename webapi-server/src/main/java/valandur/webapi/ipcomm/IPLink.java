package valandur.webapi.ipcomm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class IPLink {

    private List<Consumer<IPResponse>> listeners = new ArrayList<>();

    public abstract void init(ContextHandlerCollection handlers);

    public abstract boolean hasServer(String server);

    public abstract void send(String server, IPRequest message);

    public void respond(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            IPResponse res = mapper.readValue(message, IPResponse.class);
            listeners.forEach(c -> c.accept(res));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onResponse(Consumer<IPResponse> callback) {
        listeners.add(callback);
    }
}
