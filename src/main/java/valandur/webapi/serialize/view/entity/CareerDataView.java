package valandur.webapi.serialize.view.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.CareerData;
import org.spongepowered.api.data.type.Career;
import valandur.webapi.api.serialize.BaseView;

public class CareerDataView extends BaseView<CareerData> {

    @JsonValue
    public Career career;


    public CareerDataView(CareerData value) {
        super(value);

        this.career = value.type().get();
    }
}
