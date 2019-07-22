package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.RedstonePoweredData;
import valandur.webapi.serialize.BaseView;

public class RedstonePoweredDataView extends BaseView<RedstonePoweredData> {

    @JsonValue
    public int power;


    public RedstonePoweredDataView(RedstonePoweredData value) {
        super(value);

        this.power = value.power().get();
    }
}
