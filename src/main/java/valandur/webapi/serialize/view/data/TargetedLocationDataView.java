package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.manipulator.mutable.TargetedLocationData;
import valandur.webapi.api.serialize.BaseView;

public class TargetedLocationDataView extends BaseView<TargetedLocationData> {

    @JsonValue
    public Vector3d target;


    public TargetedLocationDataView(TargetedLocationData value) {
        super(value);

        this.target = value.target().get();
    }
}
