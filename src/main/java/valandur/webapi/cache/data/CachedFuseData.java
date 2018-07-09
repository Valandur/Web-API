package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.FuseData;
import valandur.webapi.cache.CachedObject;

@ApiModel("FuseData")
public class CachedFuseData extends CachedObject<FuseData> {

    @ApiModelProperty(value = "The total amount of time (in ticks) the fuse burns for", required = true)
    public int fuseDuration;

    @ApiModelProperty(value = "The amount of ticks remaining on this fuse", required = true)
    public int ticksRemaining;


    public CachedFuseData(FuseData value) {
        super(value);

        this.fuseDuration = value.fuseDuration().get();
        this.ticksRemaining = value.ticksRemaining().get();
    }
}
