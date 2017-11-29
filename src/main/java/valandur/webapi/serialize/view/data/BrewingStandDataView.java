package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.tileentity.BrewingStandData;
import valandur.webapi.api.serialize.BaseView;

public class BrewingStandDataView extends BaseView<BrewingStandData> {

    public int remainingBrewTime;


    public BrewingStandDataView(BrewingStandData value) {
        super(value);

        this.remainingBrewTime = value.remainingBrewTime().get();
    }
}
