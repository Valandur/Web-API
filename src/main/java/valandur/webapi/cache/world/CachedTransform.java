package valandur.webapi.cache.world;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedVector3d;
import valandur.webapi.serialize.JsonDetails;

@ApiModel("Transform")
public class CachedTransform extends CachedObject<Transform> {

    private CachedWorld world;
    @JsonDetails(value = false, simple = true)
    @ApiModelProperty(value = "The world of this transform", required = true)
    public CachedWorld getWorld() {
        return world;
    }

    private CachedVector3d position;
    @ApiModelProperty(value = "The position within the world", required = true)
    public CachedVector3d getPosition() {
        return position;
    }

    private CachedVector3d rotation;
    @ApiModelProperty(value = "The rotation of the object", required = true)
    public CachedVector3d getRotation() {
        return rotation;
    }

    private CachedVector3d scale;
    @ApiModelProperty(value = "The scale of the object", required = true)
    public CachedVector3d getScale() {
        return scale;
    }


    public CachedTransform(Transform<World> transform) {
        super(null);

        this.world = cacheService.getWorld(transform.getExtent());
        this.position = new CachedVector3d(transform.getPosition());
        this.rotation = new CachedVector3d(transform.getRotation());
        this.scale = new CachedVector3d(transform.getScale());
    }

    @Override
    public Transform getLive() {
        return new Transform<>(world.getLive(), position.getLive());
    }
}
