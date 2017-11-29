package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.VehicleData;
import org.spongepowered.api.entity.EntitySnapshot;
import valandur.webapi.api.serialize.BaseView;

public class VehicleDataView extends BaseView<VehicleData> {

    public EntitySnapshot baseVehicle;
    public EntitySnapshot vehicle;


    public VehicleDataView(VehicleData value) {
        super(value);

        this.baseVehicle = value.baseVehicle().get();
        this.vehicle = value.vehicle().get();
    }
}
