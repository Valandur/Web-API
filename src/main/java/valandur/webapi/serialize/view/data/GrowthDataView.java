package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.block.GrowthData;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("GrowthData")
public class GrowthDataView extends BaseView<GrowthData> {

    @ApiModelProperty(value = "The current growth stage of this entity", required = true)
    public int stage;


    public GrowthDataView(GrowthData value) {
        super(value);

        this.stage = value.growthStage().get();
    }
}
