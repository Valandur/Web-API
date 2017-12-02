package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.block.GrowthData;
import valandur.webapi.api.serialize.BaseView;

public class GrowthDataView extends BaseView<GrowthData> {

    public int stage;


    public GrowthDataView(GrowthData value) {
        super(value);

        this.stage = value.growthStage().get();
    }
}
