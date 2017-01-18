package valandur.webapi.cache;

import com.flowpowered.math.vector.Vector3d;
import com.google.gson.annotations.Expose;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class CachedLocation extends CachedObject {

    @Expose
    public CachedWorld world;

    @Expose
    public CachedVector3d position;

    public static CachedLocation copyFrom(Location<World> location) {
        CachedLocation cache = new CachedLocation();
        cache.world = CachedWorld.copyFrom(location.getExtent());
        cache.position = CachedVector3d.copyFrom(location.getPosition());
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return 0;
    }
    @Override
    public Optional<Object> getLive() {
        Optional<Object> w = world.getLive();
        if (!w.isPresent())
            return Optional.empty();
        Optional<Object> p = position.getLive();
        if (!p.isPresent())
            return Optional.empty();
        return Optional.of(new Location<World>((World)w.get(), (Vector3d)p.get()));
    }
}
