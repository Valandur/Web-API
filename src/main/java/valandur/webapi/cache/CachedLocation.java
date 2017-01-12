package valandur.webapi.cache;

import com.google.gson.annotations.Expose;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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
}
