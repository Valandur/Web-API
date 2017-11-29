package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.FlammableData;
import valandur.webapi.api.serialize.BaseView;

public class FlammableDataView extends BaseView<FlammableData> {

    @JsonValue
    public boolean flammable;


    public FlammableDataView(FlammableData value) {
        super(value);

        this.flammable = value.flammable().get();
    }
}
