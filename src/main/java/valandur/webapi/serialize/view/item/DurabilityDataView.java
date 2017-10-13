package valandur.webapi.serialize.view.item;

import org.spongepowered.api.data.manipulator.mutable.item.DurabilityData;
import valandur.webapi.api.serialize.BaseView;

public class DurabilityDataView extends BaseView<DurabilityData> {

    public boolean unbreakable;
    public int durability;


    public DurabilityDataView(DurabilityData value) {
        super(value);

        this.unbreakable = value.unbreakable().get();
        this.durability = value.durability().get();
    }
}
