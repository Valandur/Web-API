package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.IgniteableData;
import valandur.webapi.api.serialize.BaseView;

public class IgniteableDataView extends BaseView<IgniteableData> {

    public int fireDelay;
    public int fireTicks;


    public IgniteableDataView(IgniteableData value) {
        super(value);

        this.fireDelay = value.fireDelay().get();
        this.fireTicks = value.fireTicks().get();
    }
}
