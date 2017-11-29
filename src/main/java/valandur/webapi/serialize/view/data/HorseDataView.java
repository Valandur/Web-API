package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.HorseData;
import org.spongepowered.api.data.type.HorseColor;
import org.spongepowered.api.data.type.HorseStyle;
import valandur.webapi.api.serialize.BaseView;

public class HorseDataView extends BaseView<HorseData> {

    public HorseColor color;
    public HorseStyle style;


    public HorseDataView(HorseData value) {
        super(value);

        this.color = value.color().get();
        this.style = value.style().get();
    }
}
