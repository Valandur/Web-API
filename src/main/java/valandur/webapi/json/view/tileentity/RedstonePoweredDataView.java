package valandur.webapi.json.view.tileentity;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.RedstonePoweredData;
import valandur.webapi.api.json.BaseView;

public class RedstonePoweredDataView extends BaseView<RedstonePoweredData> {

    @JsonValue
    public int power;


    public RedstonePoweredDataView(RedstonePoweredData value) {
        super(value);

        this.power = value.power().get();
    }
}
