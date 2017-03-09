package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class CachedLocation extends CachedObject {

    @JsonProperty
    public CachedWorld world;

    @JsonProperty
    public Vector3d position;

    public static CachedLocation copyFrom(Location<World> location) {
        CachedLocation cache = new CachedLocation();
        cache.world = CachedWorld.copyFrom(location.getExtent());
        cache.position = location.getPosition().clone();
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
        return Optional.of(new Location<World>((World)w.get(), position));
    }
}
