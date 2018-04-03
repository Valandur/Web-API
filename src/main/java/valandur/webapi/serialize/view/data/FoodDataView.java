package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("FoodData")
public class FoodDataView extends BaseView<FoodData> {

    @ApiModelProperty(value = "The food level of this entity", required = true)
    public int foodLevel;

    @ApiModelProperty(value = "The saturation of this entity", required = true)
    public double saturation;

    @ApiModelProperty(value = "The exhaustion of this entity", required = true)
    public double exhaustion;


    public FoodDataView(FoodData value) {
        super(value);

        this.foodLevel = value.foodLevel().get();
        this.saturation = value.saturation().get();
        this.exhaustion = value.exhaustion().get();
    }
}
