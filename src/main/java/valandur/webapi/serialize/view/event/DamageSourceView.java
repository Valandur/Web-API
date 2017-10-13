package valandur.webapi.serialize.view.event;

import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import valandur.webapi.api.serialize.BaseView;

public class DamageSourceView extends BaseView<DamageSource> {

    public boolean affectsCreative;
    public boolean absolute;
    public boolean bypassingArmour;
    public boolean explosive;
    public boolean magic;
    public boolean scaledByDifficulty;
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
