package valandur.webapi.cache;

import com.google.gson.annotations.Expose;
import org.spongepowered.api.world.World;

import java.util.Map;

public class CachedWorld extends CachedObject {

    @Expose
    public String name;

    @Expose
    public String uuid;

    public Map<String, Object> properties;

    public static CachedWorld copyFrom(World world) {
        return copyFrom(world, false);
    }
    public static CachedWorld copyFrom(World world, boolean details) {
        CachedWorld cache = new CachedWorld();
        cache.name = world.getName();
        cache.uuid = world.getUniqueId().toString();
        if (details) {
            cache.details = true;
            cache.properties = DataCache.containerToMap(world.getProperties().toContainer());
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return CacheDurations.world;
    }
}
