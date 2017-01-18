package valandur.webapi.cache;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;
import valandur.webapi.misc.JsonConverter;

import java.util.Optional;
import java.util.UUID;

public class CachedEntity extends CachedObject {
    @Expose
    public String type;

    @Expose
    public String uuid;

    public CachedLocation location;
    public CachedVector3d velocity;
    public CachedVector3d rotation;
    public JsonElement data;
    public JsonElement properties;

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
            cache.data = JsonConverter.containerToJson(entity);
            cache.properties = JsonConverter.propertiesToJson(entity);
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.entity;
    }
    @Override
    public Optional<Object> getLive() {
        for (World w : Sponge.getServer().getWorlds()) {
            Optional<Entity> e = w.getEntity(UUID.fromString(uuid));
            if (e.isPresent())
                return Optional.of(e.get());
        }
        return Optional.empty();
    }
}
