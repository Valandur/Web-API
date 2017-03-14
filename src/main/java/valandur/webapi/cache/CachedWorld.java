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

    public String dimension;
    public JsonNode data;
    public JsonNode generator;

    public static CachedWorld copyFrom(World world) {
        return copyFrom(world, false);
    }
    public static CachedWorld copyFrom(World world, boolean details) {
        CachedWorld cache = new CachedWorld();
        cache.name = world.getName();
        cache.uuid = world.getUniqueId().toString();

        if (details) {
            cache.details = true;
            cache.dimension = world.getDimension().getType().getName();
            cache.data = JsonConverter.toJson(world.getProperties().toContainer(), true);
            cache.generator = JsonConverter.toJson(world.getProperties().getGeneratorType(), true);
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.durationWorld;
    }
    @Override
    public Optional<Object> getLive() {
        Optional<World> w = Sponge.getServer().getWorld(UUID.fromString(uuid));
        if (!w.isPresent())
            return Optional.empty();
        return Optional.of(w.get());
    }

    @Override
    @JsonProperty
    public String getLink() {
        return "/api/world/" + uuid;
    }
}
