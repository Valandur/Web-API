package valandur.webapi.json.view.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import valandur.webapi.api.json.BaseView;

public class AgeableDataView extends BaseView<AgeableData> {

    public boolean adult;
    public int age;

    public AgeableDataView(AgeableData value) {
        super(value);

        this.adult = value.adult().get();
        this.age = value.age().get();
    }
}
