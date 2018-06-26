package valandur.webapi.ipcomm;

import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class IPLink {

    private List<Consumer<IPResponse>> listeners = new ArrayList<>();

    public abstract void init();

    public abstract Class<? extends IPServlet> getServletClass();

    public abstract boolean hasServer(String server);

    public abstract void send(String server, IPRequest message);

    public void respond(IPResponse res) {
        listeners.forEach(c -> c.accept(res));
    }

    public void onResponse(Consumer<IPResponse> callback) {
        listeners.add(callback);
    }
}
