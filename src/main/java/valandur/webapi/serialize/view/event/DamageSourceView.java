package valandur.webapi.serialize.view.event;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import valandur.webapi.serialize.BaseView;

@ApiModel("DamageSource")
public class DamageSourceView extends BaseView<DamageSource> {

    @ApiModelProperty(value = "True if this damage source can affect players in creative, false otherwise", required = true)
    public boolean affectsCreative;

    @ApiModelProperty(value = "True if the damage value done by this source is absolute, false otherwise", required = true)
    public boolean absolute;

    @ApiModelProperty(value = "True if the damage done by this source disregards armour, false otherwise", required = true)
    public boolean bypassingArmour;

    @ApiModelProperty(value = "True if this source produces explosive damage, false otherwise", required = true)
    public boolean explosive;

    @ApiModelProperty(value = "True if this source produces magic damage, false otherwise", required = true)
    public boolean magic;

    @ApiModelProperty(value = "True if the damage of this source is scaled by the difficulty, false otherwise", required = true)
    public boolean scaledByDifficulty;

    @ApiModelProperty(value = "The damage type inflicted by this damage source", required = true)
    public DamageType damageType;


    public DamageSourceView(DamageSource value) {
        super(value);

        this.affectsCreative = value.doesAffectCreative();
        this.absolute = value.isAbsolute();
        this.bypassingArmour = value.isBypassingArmor();
        this.explosive = value.isExplosive();
        this.magic = value.isMagic();
        this.scaledByDifficulty = value.isScaledByDifficulty();
        this.damageType = value.getType();
    }
}
