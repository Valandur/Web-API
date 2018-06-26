package valandur.webapi.ipcomm;

import java.util.function.Function;

public interface IPLink {

    void init();

    void start();

    boolean hasServer(String server);

    boolean send(String server, IPRequest message);

    void onResponse(Function<IPResponse, Void> callback);
}
