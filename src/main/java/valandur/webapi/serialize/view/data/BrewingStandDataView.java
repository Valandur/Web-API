package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.tileentity.BrewingStandData;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("BrewingStandData")
public class BrewingStandDataView extends BaseView<BrewingStandData> {

    @ApiModelProperty(value = "The time remaining until brewing is complete", required = true)
    public int remainingBrewTime;


    public BrewingStandDataView(BrewingStandData value) {
        super(value);

        this.remainingBrewTime = value.remainingBrewTime().get();
    }
}
