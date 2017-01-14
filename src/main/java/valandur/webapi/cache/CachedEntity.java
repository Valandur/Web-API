package valandur.webapi.cache;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import org.spongepowered.api.entity.Entity;
import valandur.webapi.misc.JsonConverter;

import java.util.Map;

public class CachedEntity extends CachedObject {
    @Expose
    public String type;

    @Expose
    public String uuid;

    public CachedLocation location;
    public CachedVector3d velocity;
    public CachedVector3d rotation;

    public static CachedEntity copyFrom(Entity entity) {
        return copyFrom(entity, false);
    }
    public static CachedEntity copyFrom(Entity entity, boolean details) {
        CachedEntity cache = new CachedEntity();
        cache.type = entity.getType().getName();
        cache.uuid = entity.getUniqueId().toString();
        if (details) {
            cache.details = true;
            cache.location = CachedLocation.copyFrom(entity.getLocation());
            cache.velocity = CachedVector3d.copyFrom(entity.getVelocity());
            cache.rotation = CachedVector3d.copyFrom(entity.getRotation());
            cache.raw  = JsonConverter.toJson(entity);
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return CacheDurations.entity;
    }
}
