package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.FireworkRocketData;
import valandur.webapi.api.serialize.BaseView;

public class FireworkRocketDataView extends BaseView<FireworkRocketData> {

    public int flightModifier;


    public FireworkRocketDataView(FireworkRocketData value) {
        super(value);

        this.flightModifier = value.flightModifier().get();
    }
}
