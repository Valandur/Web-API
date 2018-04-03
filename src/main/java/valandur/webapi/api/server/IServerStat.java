package valandur.webapi.api.server;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.Instant;

@ApiModel(value = "ServerStat")
public interface IServerStat<V> {

    @ApiModelProperty(value = "The epoch timestamp (in seconds) when the statistic was recorded", dataType = "long", required = true)
    Instant getTimestamp();

    @ApiModelProperty(value = "The value that was recorded", dataType = "number", required = true)
    V getValue();
}
