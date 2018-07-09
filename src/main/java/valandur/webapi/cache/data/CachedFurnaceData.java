package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.tileentity.FurnaceData;
import valandur.webapi.cache.CachedObject;

@ApiModel("FurnaceData")
public class CachedFurnaceData extends CachedObject<FurnaceData> {

    @ApiModelProperty(value = "The maximum amount of time (in ticks) the current fuel item lasts", required = true)
    public int maxBurnTime;

    @ApiModelProperty(value = "The total amount of time (in ticks) the stack has to cook for to be done", required = true)
    public int maxCookTime;

    @ApiModelProperty(value = "The amount of time (in ticks) that has passed since this fuel item started burning", required = true)
    public int passedBurnTime;

    @ApiModelProperty(value = "The amount of time (in ticks) that has passed since the item stack started cooking", required = true)
    public int passedCookTime;


    public CachedFurnaceData(FurnaceData value) {
        super(value);

        this.maxBurnTime = value.maxBurnTime().get();
        this.maxCookTime = value.maxCookTime().get();
        this.passedBurnTime = value.passedBurnTime().get();
        this.passedCookTime = value.passedCookTime().get();
    }
}
