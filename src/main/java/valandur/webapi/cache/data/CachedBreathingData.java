package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.BreathingData;
import valandur.webapi.cache.CachedObject;

@ApiModel("BreathingData")
public class CachedBreathingData extends CachedObject<BreathingData> {

    @ApiModelProperty(value = "The maximum amount of air available to this entity", required = true)
    public int max;

    @ApiModelProperty(value = "The amount of air currently remaining", required = true)
    public int remaining;


    public CachedBreathingData(BreathingData value) {
        super(value);

        this.max = value.maxAir().get();
        this.remaining = value.remainingAir().get();
    }
}
