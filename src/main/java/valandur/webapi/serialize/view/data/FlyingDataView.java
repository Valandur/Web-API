package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingData;
import valandur.webapi.serialize.BaseView;

public class FlyingDataView extends BaseView<FlyingData> {

    @JsonValue
    public boolean flying;


    public FlyingDataView(FlyingData value) {
        super(value);

        this.flying = value.flying().get();
    }
}
