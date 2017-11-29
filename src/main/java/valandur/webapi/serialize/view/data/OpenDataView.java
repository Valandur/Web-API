package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.OpenData;
import valandur.webapi.api.serialize.BaseView;

public class OpenDataView extends BaseView<OpenData> {

    @JsonValue
    public boolean open;


    public OpenDataView(OpenData value) {
        super(value);

        this.open = value.open().get();
    }
}
