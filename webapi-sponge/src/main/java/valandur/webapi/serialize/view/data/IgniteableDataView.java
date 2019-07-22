package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.IgniteableData;
import valandur.webapi.serialize.BaseView;

@ApiModel("IgniteableData")
public class IgniteableDataView extends BaseView<IgniteableData> {

    @ApiModelProperty(value = "The delay of the fire", required = true)
    public int fireDelay;

    @ApiModelProperty(value = "The amount of ticks the fire will burn for", required = true)
    public int fireTicks;


    public IgniteableDataView(IgniteableData value) {
        super(value);

        this.fireDelay = value.fireDelay().get();
        this.fireTicks = value.fireTicks().get();
    }
}
