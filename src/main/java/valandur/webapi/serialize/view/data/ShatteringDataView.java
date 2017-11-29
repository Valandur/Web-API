package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ShatteringData;
import valandur.webapi.api.serialize.BaseView;

public class ShatteringDataView extends BaseView<ShatteringData> {

    @JsonValue
    public boolean shatters;


    public ShatteringDataView(ShatteringData value) {
        super(value);

        this.shatters = value.willShatter().get();
    }
}
