package valandur.webapi.serialize.view.data;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.mutable.entity.DamageableData;
import org.spongepowered.api.entity.Entity;
import valandur.webapi.api.serialize.BaseView;

@ApiModel("DamageableData")
public class DamageableDataView extends BaseView<DamageableData> {

    @ApiModelProperty("The entity which last attacked this entity")
    public Entity lastAttacker;

    @ApiModelProperty("The amount of damage inflicted by the last attacker")
    public Double lastDamage;


    public DamageableDataView(DamageableData value) {
        super(value);

        this.lastAttacker = value.lastAttacker().get().orElse(null);
        this.lastDamage = value.lastDamage().get().orElse(null);
    }
}
