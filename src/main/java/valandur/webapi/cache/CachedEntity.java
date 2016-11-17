package valandur.webapi.cache;

import com.google.gson.annotations.Expose;
import org.spongepowered.api.entity.Entity;

import java.util.Map;

public class CachedEntity {
    @Expose
    public String type;

    @Expose
    public String uuid;

    public CachedLocation location;
    public CachedVector3d velocity;
    public CachedVector3d rotation;
    public Map<String, Object> data;
    public Map<String, Object> properties;

    public static CachedEntity copyFrom(Entity entity) {
        return copyFrom(entity, false);
    }
    public static CachedEntity copyFrom(Entity entity, boolean details) {
        CachedEntity cache = new CachedEntity();
        cache.type = entity.getType().getName();
        cache.uuid = entity.getUniqueId().toString();
        if (details) {
            cache.location = CachedLocation.copyFrom(entity.getLocation());
            cache.velocity = CachedVector3d.copyFrom(entity.getVelocity());
            cache.rotation = CachedVector3d.copyFrom(entity.getRotation());
            cache.data = DataCache.containerToMap(entity);
            cache.properties = DataCache.propertiesToMap(entity);
        }
        return cache;
    }
}
