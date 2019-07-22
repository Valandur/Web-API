package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingAbilityData;
import valandur.webapi.serialize.BaseView;

public class FlyingAbilityDataView extends BaseView<FlyingAbilityData> {

    @JsonValue
    public boolean canFly;


    public FlyingAbilityDataView(FlyingAbilityData value) {
        super(value);

        this.canFly = value.canFly().get();
    }
}
