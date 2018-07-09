package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import valandur.webapi.cache.CachedObject;

@ApiModel("HealthData")
public class CachedHealthData extends CachedObject<HealthData> {

    @ApiModelProperty(value = "The current health of the entity", required = true)
    public double current;

    @ApiModelProperty(value = "The maximum health of the entity", required = true)
    public double max;


    public CachedHealthData(HealthData value) {
        super(value);

        this.current = value.health().get();
        this.max = value.maxHealth().get();
    }
}
