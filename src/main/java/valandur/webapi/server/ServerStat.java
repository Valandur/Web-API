package valandur.webapi.server;

import valandur.webapi.api.server.IServerStat;

import java.time.Instant;

public class ServerStat<V> implements IServerStat<V> {

    private Instant timestamp;
    public Instant getTimestamp() {
        return timestamp;
    }

    private V value;
    public V getValue() {
        return value;
    }


    public ServerStat(V value) {
        this.timestamp = Instant.now();
        this.value = value;
    }
}
