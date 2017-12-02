package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.PickupDelayData;
import valandur.webapi.api.serialize.BaseView;

public class PickupDelayDataView extends BaseView<PickupDelayData> {

    public int delay;
    public boolean infinite;


    public PickupDelayDataView(PickupDelayData value) {
        super(value);

        this.delay = value.delay().get();
        this.infinite = value.infinite().get();
    }
}
