package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import valandur.webapi.json.JsonConverter;

import java.util.Optional;
import java.util.UUID;

public class CachedWorld extends CachedObject {

    @JsonProperty
    public String name;

    @JsonProperty
    public String uuid;

    public JsonNode dimension;
    public JsonNode data;
    public JsonNode generator;


    public CachedWorld(World world) {
        this.name = world.getName();
        this.uuid = world.getUniqueId().toString();
        this.dimension = JsonConverter.toJson(world.getDimension());
        this.data = JsonConverter.toJson(world.getProperties().toContainer());
        this.generator = JsonConverter.toJson(world.getProperties().getGeneratorType());
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.durationWorld;
    }
    @Override
    public Optional<?> getLive() {
        return Sponge.getServer().getWorld(UUID.fromString(uuid));
    }

    @Override
    @JsonProperty
    public String getLink() {
        return "/api/world/" + uuid;
    }
}
