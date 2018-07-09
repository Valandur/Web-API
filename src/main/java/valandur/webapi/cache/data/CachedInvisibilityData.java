package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import valandur.webapi.cache.CachedObject;

@ApiModel("InvisibilityData")
public class CachedInvisibilityData extends CachedObject<InvisibilityData> {

    @ApiModelProperty(value = "True if this entity ignores collisions, false otherwise", required = true)
    public boolean ignoreCollision;

    @ApiModelProperty(value = "True if this entity is invisible, false otherwise", required = true)
    public boolean invisible;

    @ApiModelProperty(value = "True if this entity is not targetable, false otherwise", required = true)
    public boolean untargetable;

    @ApiModelProperty(value = "True if this entity is vanished, false otherwise", required = true)
    public boolean vanish;


    public CachedInvisibilityData(InvisibilityData value) {
        super(value);

        this.ignoreCollision = value.ignoresCollisionDetection().get();
        this.invisible = value.invisible().get();
        this.untargetable = value.untargetable().get();
        this.vanish = value.vanish().get();
    }
}
