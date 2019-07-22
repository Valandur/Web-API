package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.CustomNameVisibleData;
import valandur.webapi.serialize.BaseView;

public class CustomNameVisibleDataView extends BaseView<CustomNameVisibleData> {

    @JsonValue
    public boolean visible;


    public CustomNameVisibleDataView(CustomNameVisibleData value) {
        super(value);

        this.visible = value.customNameVisible().get();
    }
}
