package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.WetData;
import valandur.webapi.serialize.BaseView;

public class WetDataView extends BaseView<WetData> {

    @JsonValue
    public boolean wet;


    public WetDataView(WetData value) {
        super(value);

        this.wet = value.wet().get();
    }
}
