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

    public JsonNode border;
    public JsonNode difficulty;
    public JsonNode dimension;
    public JsonNode gameMode;
    public JsonNode gameRules;
    public JsonNode generator;
    public JsonNode seed;
    public JsonNode spawn;
    public JsonNode time;
    public JsonNode weather;


    public CachedWorld(World world) {
        this.name = world.getName();
        this.uuid = world.getUniqueId().toString();

        this.border = JsonConverter.toJson(world.getWorldBorder());
        this.difficulty = JsonConverter.toJson(world.getDifficulty().getId());
        this.dimension = JsonConverter.toJson(world.getDimension());
        this.gameMode = JsonConverter.toJson(world.getProperties().getGameMode().getId());
        this.gameRules = JsonConverter.toJson(world.getGameRules());
        this.generator = JsonConverter.toJson(world.getProperties().getGeneratorType());
        this.seed = JsonConverter.toJson(world.getProperties().getSeed());
        this.spawn = JsonConverter.toJson(world.getProperties().getSpawnPosition());
        this.time = JsonConverter.toJson(world.getProperties().getWorldTime());
        this.weather = JsonConverter.toJson(world.getWeather().getId());
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
