package valandur.webapi.serialize.view.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.world.explosion.Explosion;
import valandur.webapi.serialize.BaseView;

@ApiModel("Explosion")
public class ExplosionView extends BaseView<Explosion> {

    @ApiModelProperty(value = "True if this explosion causes fires, false otherwise", required = true)
    public boolean causeFire;

    @ApiModelProperty(value = "The radius of this explosion", required = true)
    public float radius;

    @ApiModelProperty(value = "True if this explosion breaks blocks, false otherwise", required = true)
    public boolean breakBlocks;

    @ApiModelProperty(value = "True if this explosion damages entities, false otherwise", required = true)
    public boolean damageEntities;

    @ApiModelProperty(value = "True if a smoke animation is played for this explosion, false otherwise", required = true)
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
