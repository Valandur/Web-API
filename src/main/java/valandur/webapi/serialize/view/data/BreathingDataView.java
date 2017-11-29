package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.BreathingData;
import valandur.webapi.api.serialize.BaseView;

public class BreathingDataView extends BaseView<BreathingData> {

    public int max;
    public int remaining;


    public BreathingDataView(BreathingData value) {
        super(value);

        this.max = value.maxAir().get();
        this.remaining = value.remainingAir().get();
    }
}
