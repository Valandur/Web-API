package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.PickupDelayData;
import valandur.webapi.serialize.BaseView;

@ApiModel("PickupDelayData")
public class PickupDelayDataView extends BaseView<PickupDelayData> {

    @ApiModelProperty(value = "The delay that entities must wait to pick up this entity", required = true)
    public int delay;

    @ApiModelProperty(value = "True if other entities can never pick up this entity, false otherwise", required = true)
    public boolean infinite;


    public PickupDelayDataView(PickupDelayData value) {
        super(value);

        this.delay = value.delay().get();
        this.infinite = value.infinite().get();
    }
}
