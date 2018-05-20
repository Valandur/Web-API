package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.FireworkRocketData;
import valandur.webapi.serialize.BaseView;

@ApiModel("FireworkRocketData")
public class FireworkRocketDataView extends BaseView<FireworkRocketData> {

    @ApiModelProperty(value = "The flight modifier of this firework rocket", required = true)
    public int flightModifier;


    public FireworkRocketDataView(FireworkRocketData value) {
        super(value);

        this.flightModifier = value.flightModifier().get();
    }
}
