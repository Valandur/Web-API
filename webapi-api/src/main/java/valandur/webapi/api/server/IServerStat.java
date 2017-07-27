package valandur.webapi.api.server;

import java.time.Instant;

public interface IServerStat<V> {

    Instant getTimestamp();

    V getValue();
}
