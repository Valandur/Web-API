package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.FuseData;
import valandur.webapi.api.serialize.BaseView;

public class FuseDataView extends BaseView<FuseData> {

    public int fuseDuration;
    public int ticksRemaining;


    public FuseDataView(FuseData value) {
        super(value);

        this.fuseDuration = value.fuseDuration().get();
        this.ticksRemaining = value.ticksRemaining().get();
    }
}
