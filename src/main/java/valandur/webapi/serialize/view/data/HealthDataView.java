package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import valandur.webapi.api.serialize.BaseView;

public class HealthDataView extends BaseView<HealthData> {

    public double current;
    public double max;


    public HealthDataView(HealthData value) {
        super(value);

        this.current = value.health().get();
        this.max = value.maxHealth().get();
    }
}
