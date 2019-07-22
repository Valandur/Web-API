package valandur.webapi.server;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.Instant;

@ApiModel(value = "ServerStat")
public class ServerStat<V> {

    private Instant timestamp;
    @ApiModelProperty(value = "The epoch timestamp (in seconds) when the statistic was recorded", dataType = "long", required = true)
    public Instant getTimestamp() {
        return timestamp;
    }

    private V value;
    @ApiModelProperty(value = "The value that was recorded", dataType = "number", required = true)
    public V getValue() {
        return value;
    }


    public ServerStat(V value) {
        this.timestamp = Instant.now();
        this.value = value;
    }
}
