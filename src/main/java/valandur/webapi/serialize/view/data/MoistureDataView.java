package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.MoistureData;
import valandur.webapi.serialize.BaseView;

public class MoistureDataView extends BaseView<MoistureData> {

    @JsonValue
    public int moisture;


    public MoistureDataView(MoistureData value) {
        super(value);

        this.moisture = value.moisture().get();
    }
}
