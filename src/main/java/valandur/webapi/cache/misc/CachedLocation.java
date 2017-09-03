package valandur.webapi.cache.misc;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.misc.ICachedLocation;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.cache.CachedObject;

import java.util.Optional;

public class CachedLocation extends CachedObject implements ICachedLocation {

    private ICachedWorld world;
    @Override
    public ICachedWorld getWorld() {
        return world;
    }

    private Vector3d position;
    @Override
    public Vector3d getPosition() {
        return position;
    }


    public CachedLocation(Location<World> location) {
        super(null);

        this.world = WebAPI.getCacheService().getWorld(location.getExtent());
        this.position = location.getPosition().clone();
    }

    @Override
    public Optional<?> getLive() {
        Optional<?> w = world.getLive();
        return w.map(o -> new Location<>((World) o, position));
    }
}
