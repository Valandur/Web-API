package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.VehicleData;
import org.spongepowered.api.entity.EntitySnapshot;
import valandur.webapi.serialize.BaseView;

@ApiModel("VehicleData")
public class VehicleDataView extends BaseView<VehicleData> {

    @ApiModelProperty(value = "The base vehicle entity", required = true)
    public EntitySnapshot baseVehicle;

    @ApiModelProperty(value = "The vehicle entity itself", required = true)
    public EntitySnapshot vehicle;


    public VehicleDataView(VehicleData value) {
        super(value);

        this.baseVehicle = value.baseVehicle().get();
        this.vehicle = value.vehicle().get();
    }
}
