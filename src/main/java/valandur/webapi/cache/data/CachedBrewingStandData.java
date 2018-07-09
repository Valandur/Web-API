package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BrewingStandData;
import valandur.webapi.cache.CachedObject;

@ApiModel("BrewingStandData")
public class CachedBrewingStandData extends CachedObject<BrewingStandData> {

    @ApiModelProperty(value = "The time remaining until brewing is complete", required = true)
    public int remainingBrewTime;


    public CachedBrewingStandData(BrewingStandData value) {
        super(value);

        this.remainingBrewTime = value.remainingBrewTime().get();
    }
}
