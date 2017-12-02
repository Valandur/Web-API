package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.LayeredData;
import valandur.webapi.api.serialize.BaseView;

public class LayeredDataView extends BaseView<LayeredData> {

    @JsonValue
    public int layer;


    public LayeredDataView(LayeredData value) {
        super(value);

        this.layer = value.layer().get();
    }
}
