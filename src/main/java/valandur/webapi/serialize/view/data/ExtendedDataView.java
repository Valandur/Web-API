package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.ExtendedData;
import valandur.webapi.serialize.BaseView;

public class ExtendedDataView extends BaseView<ExtendedData> {

    @JsonValue
    public boolean extended;


    public ExtendedDataView(ExtendedData value) {
        super(value);

        this.extended = value.extended().get();
    }
}
