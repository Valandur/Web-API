package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.JoinData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedInstant;

@ApiModel("JoinData")
public class CachedJoinData extends CachedObject<JoinData> {

    @ApiModelProperty(value = "The first time this entity joined the server", required = true)
    public CachedInstant first;

    @ApiModelProperty(value = "The most recent time this entity joined the server", required = true)
    public CachedInstant last;


    public CachedJoinData(JoinData value) {
        super(value);

        this.first = new CachedInstant(value.firstPlayed().get());
        this.last = new CachedInstant(value.lastPlayed().get());
    }
}
