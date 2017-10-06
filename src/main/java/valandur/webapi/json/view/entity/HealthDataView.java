package valandur.webapi.json.view.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import valandur.webapi.api.json.BaseView;

public class HealthDataView extends BaseView<HealthData> {

    public double current;
    public double max;


    public HealthDataView(HealthData value) {
        super(value);

        this.current = value.health().get();
        this.max = value.maxHealth().get();
    }
}
