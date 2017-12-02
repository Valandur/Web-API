package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.ArmorStandData;
import valandur.webapi.api.serialize.BaseView;

public class ArmorStandDataView extends BaseView<ArmorStandData> {

    public boolean arms;
    public boolean basePlate;
    public boolean marker;
    public boolean small;


    public ArmorStandDataView(ArmorStandData value) {
        super(value);

        this.arms = value.arms().get();
        this.basePlate = value.basePlate().get();
        this.marker = value.marker().get();
        this.small = value.small().get();
    }
}
