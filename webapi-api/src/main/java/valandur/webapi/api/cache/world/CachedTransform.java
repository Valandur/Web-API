package valandur.webapi.api.cache.world;

import com.flowpowered.math.vector.Vector3d;
import io.swagger.annotations.ApiModel;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.Optional;

@ApiModel("Transform")
public class CachedTransform extends CachedObject<Transform> {

    private ICachedWorld world;
    @JsonDetails(value = false, simple = true)
    public ICachedWorld getWorld() {
        return world;
    }

    private Vector3d position;
    public Vector3d getPosition() {
        return position;
    }

    private Vector3d rotation;
    public Vector3d getRotation() {
        return rotation;
    }

    private Vector3d scale;
    public Vector3d getScale() {
        return scale;
    }


    public CachedTransform(Transform<World> transform) {
        super(null);

        this.world = cacheService.getWorld(transform.getExtent());
        this.position = transform.getPosition().clone();
        this.rotation = transform.getRotation().clone();
        this.scale = transform.getScale().clone();
    }

    @Override
    public Optional<Transform> getLive() {
        Optional<World> optWorld = world.getLive();
        return optWorld.map(w -> new Transform<>(w, position));
    }
}
