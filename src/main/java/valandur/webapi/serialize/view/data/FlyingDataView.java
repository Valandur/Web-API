package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.FlyingData;
import valandur.webapi.api.serialize.BaseView;

public class FlyingDataView extends BaseView<FlyingData> {

    public boolean flying;


    public FlyingDataView(FlyingData value) {
        super(value);

        this.flying = value.flying().get();
    }
}
