package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.FireworkRocketData;
import valandur.webapi.cache.CachedObject;

@ApiModel("FireworkRocketData")
public class CachedFireworkRocketData extends CachedObject<FireworkRocketData> {

    @ApiModelProperty(value = "The flight modifier of this firework rocket", required = true)
    public int flightModifier;


    public CachedFireworkRocketData(FireworkRocketData value) {
        super(value);

        this.flightModifier = value.flightModifier().get();
    }
}
