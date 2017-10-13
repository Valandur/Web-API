package valandur.webapi.serialize.view.tileentity;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.PoweredData;
import valandur.webapi.api.serialize.BaseView;

public class PoweredDataView extends BaseView<PoweredData> {

    @JsonValue
    public boolean powered;


    public PoweredDataView(PoweredData value) {
        super(value);

        this.powered = value.powered().get();
    }
}
