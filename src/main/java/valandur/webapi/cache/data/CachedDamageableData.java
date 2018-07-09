package valandur.webapi.cache.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.DamageableData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.entity.CachedEntitySnapshot;

@ApiModel("DamageableData")
public class CachedDamageableData extends CachedObject<DamageableData> {

    @ApiModelProperty("The entity which last attacked this entity")
    public CachedEntitySnapshot lastAttacker;

    @ApiModelProperty("The amount of damage inflicted by the last attacker")
    public Double lastDamage;


    public CachedDamageableData(DamageableData value) {
        super(value);

        this.lastAttacker = value.lastAttacker().get().map(CachedEntitySnapshot::new).orElse(null);
        this.lastDamage = value.lastDamage().get().orElse(null);
    }
}
