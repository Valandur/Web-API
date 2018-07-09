package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.VehicleData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.entity.CachedEntitySnapshot;

@ApiModel("VehicleData")
public class CachedVehicleData extends CachedObject<VehicleData> {

    @ApiModelProperty(value = "The base vehicle entity", required = true)
    public CachedEntitySnapshot baseVehicle;

    @ApiModelProperty(value = "The vehicle entity itself", required = true)
    public CachedEntitySnapshot vehicle;


    public CachedVehicleData(VehicleData value) {
        super(value);

        this.baseVehicle = new CachedEntitySnapshot(value.baseVehicle().get());
        this.vehicle = new CachedEntitySnapshot(value.vehicle().get());
    }
}
