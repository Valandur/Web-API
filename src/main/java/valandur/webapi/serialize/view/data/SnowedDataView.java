package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.SnowedData;
import valandur.webapi.serialize.BaseView;

public class SnowedDataView extends BaseView<SnowedData> {

    @JsonValue
    public boolean snow;


    public SnowedDataView(SnowedData value) {
        super(value);

        this.snow = value.hasSnow().get();
    }
}
