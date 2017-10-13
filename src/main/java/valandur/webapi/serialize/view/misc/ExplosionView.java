package valandur.webapi.serialize.view.misc;

import org.spongepowered.api.world.explosion.Explosion;
import valandur.webapi.api.serialize.BaseView;

public class ExplosionView extends BaseView<Explosion> {

    public boolean causeFire;
    public float radius;
    public boolean breakBlocks;
    public boolean damageEntities;
    public boolean playSmoke;


    public ExplosionView(Explosion value) {
        super(value);

        this.causeFire = value.canCauseFire();
        this.radius = value.getRadius();
        this.breakBlocks = value.shouldBreakBlocks();
        this.damageEntities = value.shouldDamageEntities();
        this.playSmoke = value.shouldPlaySmoke();
    }
}
