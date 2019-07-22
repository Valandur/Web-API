package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.PoweredData;
import valandur.webapi.serialize.BaseView;

public class PoweredDataView extends BaseView<PoweredData> {

    @JsonValue
    public boolean powered;


    public PoweredDataView(PoweredData value) {
        super(value);

        this.powered = value.powered().get();
    }
}
