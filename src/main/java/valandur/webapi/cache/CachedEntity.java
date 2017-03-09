package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;
import valandur.webapi.json.JsonConverter;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class CachedEntity extends CachedObject {

    @JsonProperty
    public String type;
    @JsonProperty
    public String uuid;
    @JsonProperty
    public CachedLocation location;

    public Vector3d velocity;
    public Vector3d rotation;
    public JsonNode properties;
    public JsonNode data;

    public static CachedEntity copyFrom(Entity entity) {
        return copyFrom(entity, false);
    }
    public static CachedEntity copyFrom(Entity entity, boolean details) {
        CachedEntity cache = new CachedEntity();
        cache.type = entity.getType().getName();
        cache.uuid = entity.getUniqueId().toString();
        cache.location = CachedLocation.copyFrom(entity.getLocation());

        if (details) {
            cache.details = true;
            cache.velocity = entity.getVelocity().clone();
            cache.rotation = entity.getRotation().clone();
            cache.properties = JsonConverter.toJson(entity.getApplicableProperties(), true);
            cache.data = JsonConverter.toJson(entity.toContainer(), true);
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

    @Override
    @JsonProperty
    public String getLink() {
        return "/api/entity/" + uuid;
    }
}
