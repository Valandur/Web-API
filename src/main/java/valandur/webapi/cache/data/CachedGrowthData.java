package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.block.GrowthData;
import valandur.webapi.cache.CachedObject;

@ApiModel("GrowthData")
public class CachedGrowthData extends CachedObject<GrowthData> {

    @ApiModelProperty(value = "The current growth stage of this entity", required = true)
    public int stage;


    public CachedGrowthData(GrowthData value) {
        super(value);

        this.stage = value.growthStage().get();
    }
}
