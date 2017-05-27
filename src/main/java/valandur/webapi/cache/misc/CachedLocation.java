package valandur.webapi.cache.misc;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.DataCache;
import valandur.webapi.cache.world.CachedWorld;

import java.util.Optional;

public class CachedLocation extends CachedObject {

    private CachedWorld world;
    public CachedWorld getWorld() {
        return world;
    }

    private Vector3d position;
    public Vector3d getPosition() {
        return position;
    }


    public CachedLocation(Location<World> location) {
        super(null);

        this.world = DataCache.getWorld(location.getExtent());
        this.position = location.getPosition().clone();
    }

    @Override
    public Optional<?> getLive() {
        Optional<?> w = world.getLive();
        return w.map(o -> new Location<>((World) o, position));
    }
}
