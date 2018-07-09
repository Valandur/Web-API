package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.LeashData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.entity.CachedEntity;

@ApiModel("LeashData")
public class CachedLeashData extends CachedObject<LeashData> {

    @ApiModelProperty(value = "The holder of this entity's leash", required = true)
    public CachedEntity holder;


    public CachedLeashData(LeashData value) {
        super(value);

        this.holder = new CachedEntity(value.leashHolder().get());
    }
}
