package valandur.webapi.serialize.view.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.DyeableData;
import org.spongepowered.api.data.type.DyeColor;
import valandur.webapi.api.serialize.BaseView;

public class DyeableDataView extends BaseView<DyeableData> {

    @JsonValue
    public DyeColor color;


    public DyeableDataView(DyeableData value) {
        super(value);

        this.color = value.type().get();
    }
}
