package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.SeamlessData;
import valandur.webapi.serialize.BaseView;

public class SeamlessDataView extends BaseView<SeamlessData> {

    @JsonValue
    public boolean seamless;


    public SeamlessDataView(SeamlessData value) {
        super(value);

        this.seamless = value.seamless().get();
    }
}
