package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.ColoredData;
import org.spongepowered.api.util.Color;
import valandur.webapi.api.serialize.BaseView;

public class ColoredDataView extends BaseView<ColoredData> {

    @JsonValue
    public Color color;


    public ColoredDataView(ColoredData value) {
        super(value);

        this.color = value.color().get();
    }
}
