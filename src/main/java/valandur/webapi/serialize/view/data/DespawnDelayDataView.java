package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.DespawnDelayData;
import valandur.webapi.api.serialize.BaseView;

public class DespawnDelayDataView extends BaseView<DespawnDelayData> {

    public int delay;
    public boolean infinite;


    public DespawnDelayDataView(DespawnDelayData value) {
        super(value);

        this.delay = value.delay().get();
        this.infinite = value.infinite().get();
    }
}
