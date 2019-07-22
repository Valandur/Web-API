package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.FallDistanceData;
import valandur.webapi.serialize.BaseView;

public class FallDistanceDataView extends BaseView<FallDistanceData> {

    @JsonValue
    public float fallDistance;


    public FallDistanceDataView(FallDistanceData value) {
        super(value);

        this.fallDistance = value.fallDistance().get();
    }
}
