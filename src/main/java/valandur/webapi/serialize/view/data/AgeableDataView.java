package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import valandur.webapi.api.serialize.BaseView;

public class AgeableDataView extends BaseView<AgeableData> {

    public boolean adult;
    public int age;

    public AgeableDataView(AgeableData value) {
        super(value);

        this.adult = value.adult().get();
        this.age = value.age().get();
    }
}
