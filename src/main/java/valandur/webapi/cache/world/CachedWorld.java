package valandur.webapi.cache.world;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import valandur.webapi.cache.CacheConfig;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CachedWorld extends CachedObject {

    private UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    private String name;
    public String getName() {
        return name;
    }

    private CachedWorldBorder border;
    public CachedWorldBorder getBorder() {
        return border;
    }

    private CachedCatalogType difficulty;
    public CachedCatalogType getDifficulty() {
        return difficulty;
    }

    private CachedDimension dimension;
    public CachedDimension getDimension() {
        return dimension;
    }

    private CachedCatalogType gameMode;
    public CachedCatalogType getGameMode() {
        return gameMode;
    }

    private Map<String, String> gameRules;
    public Map<String, String> getGameRules() {
        return gameRules;
    }

    private CachedGeneratorType generatorType;
    public CachedGeneratorType getGeneratorType() {
        return generatorType;
    }

    private long seed;
    public long getSeed() {
        return seed;
    }

    private Vector3i spawn;
    public Vector3i getSpawn() {
        return spawn;
    }

    private long time;
    public long getTime() {
        return time;
    }

    private CachedCatalogType weather;
    public CachedCatalogType getWeather() {
        return weather;
    }


    public CachedWorld(World world) {
        super(world);

        this.uuid = UUID.fromString(world.getUniqueId().toString());
        this.name = world.getName();
        this.border = new CachedWorldBorder(world.getWorldBorder());
        this.difficulty = new CachedCatalogType(world.getDifficulty());
        this.dimension = new CachedDimension(world.getDimension());
        this.gameMode = new CachedCatalogType(world.getProperties().getGameMode());
        this.gameRules = new HashMap<>(world.getGameRules());
        this.generatorType = new CachedGeneratorType(world.getProperties().getGeneratorType());
        this.seed = world.getProperties().getSeed();
        this.spawn = world.getProperties().getSpawnPosition();
        this.time = world.getProperties().getWorldTime();
        this.weather = new CachedCatalogType(world.getWeather());
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.durationWorld;
    }
    @Override
    public Optional<?> getLive() {
        return Sponge.getServer().getWorld(uuid);
    }

    @Override
    public String getLink() {
        return "/api/world/" + uuid;
    }
}
