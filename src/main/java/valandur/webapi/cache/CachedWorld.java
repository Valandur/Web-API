package valandur.webapi.cache;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import org.spongepowered.api.world.World;
import valandur.webapi.misc.JsonConverter;

import java.util.Map;

public class CachedWorld extends CachedObject {

    @Expose
    public String name;

    @Expose
    public String uuid;

    public JsonElement properties;

    public static CachedWorld copyFrom(World world) {
        return copyFrom(world, false);
    }
    public static CachedWorld copyFrom(World world, boolean details) {
        CachedWorld cache = new CachedWorld();
        cache.name = world.getName();
        cache.uuid = world.getUniqueId().toString();
        if (details) {
            cache.details = true;
            cache.raw = JsonConverter.toJson(world);
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return CacheDurations.world;
    }
}
