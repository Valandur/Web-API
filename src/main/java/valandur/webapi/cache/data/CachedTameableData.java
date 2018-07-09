package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.TameableData;
import valandur.webapi.cache.CachedObject;

import java.util.UUID;

@ApiModel("TameableData")
public class CachedTameableData extends CachedObject<TameableData> {

    @ApiModelProperty(value = "True if this entity is tamed, false otherwise", required = true)
    public boolean tamed;

    @ApiModelProperty("The UUID of the entity which tamed this entity")
    public UUID owner;


    public CachedTameableData(TameableData value) {
        super(value);

        this.tamed = value.owner().exists();
        this.owner = value.owner().get().orElse(null);
    }
}
