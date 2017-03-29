package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;
import valandur.webapi.json.JsonConverter;

import java.util.Optional;
import java.util.UUID;

public class CachedEntity extends CachedObject {

    @JsonProperty
    public String type;

    @JsonProperty("class")
    public String clazz;

    @JsonProperty
    public String uuid;

    @JsonProperty
    public JsonNode location;

    public Vector3d velocity;
    public Vector3d rotation;
    public JsonNode properties;
    public JsonNode data;


    public CachedEntity(Entity entity) {
        this.type = entity.getType() != null ? entity.getType().getName() : null;
        this.clazz = entity.getClass().getName();
        this.uuid = entity.getUniqueId().toString();
        this.location = JsonConverter.toJson(new CachedLocation(entity.getLocation()));
        this.velocity = entity.getVelocity().clone();
        this.rotation = entity.getRotation().clone();
        this.properties = JsonConverter.toJson(entity.getApplicableProperties(), true);
        this.data = JsonConverter.toJson(entity.toContainer(), true);
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.durationEntity;
    }
    @Override
    public Optional<?> getLive() {
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
