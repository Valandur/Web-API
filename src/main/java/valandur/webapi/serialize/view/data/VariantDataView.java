package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.VariantData;
import valandur.webapi.serialize.BaseView;

public class VariantDataView extends BaseView<VariantData> {

    @JsonValue
    public Object type;


    public VariantDataView(VariantData value) {
        super(value);

        this.type = value.type().get();
    }
}
