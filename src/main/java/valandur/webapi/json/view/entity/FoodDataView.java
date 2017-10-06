package valandur.webapi.json.view.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.FoodData;
import valandur.webapi.api.json.BaseView;

public class FoodDataView extends BaseView<FoodData> {

    public int foodLevel;
    public double saturation;
    public double exhaustion;


    public FoodDataView(FoodData value) {
        super(value);

        this.foodLevel = value.foodLevel().get();
        this.saturation = value.saturation().get();
        this.exhaustion = value.exhaustion().get();
    }
}
