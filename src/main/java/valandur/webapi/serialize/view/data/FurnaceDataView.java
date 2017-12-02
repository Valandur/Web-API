package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.tileentity.FurnaceData;
import valandur.webapi.api.serialize.BaseView;

public class FurnaceDataView extends BaseView<FurnaceData> {

    public int maxBurnTime;
    public int maxCookTime;
    public int passedBurnTime;
    public int passedCookTime;


    public FurnaceDataView(FurnaceData value) {
        super(value);

        this.maxBurnTime = value.maxBurnTime().get();
        this.maxCookTime = value.maxCookTime().get();
        this.passedBurnTime = value.passedBurnTime().get();
        this.passedCookTime = value.passedCookTime().get();
    }
}
