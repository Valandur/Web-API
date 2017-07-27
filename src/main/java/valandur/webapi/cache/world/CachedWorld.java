package valandur.webapi.cache.world;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.api.cache.misc.ICachedCatalogType;
import valandur.webapi.api.cache.world.ICachedGeneratorType;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.cache.world.ICachedWorldBorder;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CachedWorld extends CachedObject implements ICachedWorld {

    private UUID uuid;
    public UUID getUUID() {
        return uuid;
    }

    private String name;
    public String getName() {
        return name;
    }

    private boolean loaded;
    public boolean isLoaded() {
        return loaded;
    }

    private boolean loadOnStartup;
    public boolean doesLoadOnStartup() {
        return loadOnStartup;
    }

    private boolean keepSpawnLoaded;
    public boolean doesKeepSpawnLoaded() {
        return keepSpawnLoaded;
    }

    private boolean allowCommands;
    public boolean doesAllowCommands() {
        return allowCommands;
    }

    private boolean generateBonusChests;
    public boolean doesGenerateBonusChests() {
        return generateBonusChests;
    }

    private boolean mapFeaturesEnabled;
    public boolean areMapFeaturesEnabled() {
        return mapFeaturesEnabled;
    }

    private ICachedWorldBorder border;
    public ICachedWorldBorder getBorder() {
        return border;
    }

    private ICachedCatalogType difficulty;
    public ICachedCatalogType getDifficulty() {
        return difficulty;
    }

    private ICachedCatalogType dimensionType;
    public ICachedCatalogType getDimensionType() {
        return dimensionType;
    }

    private ICachedCatalogType gameMode;
    public ICachedCatalogType getGameMode() {
        return gameMode;
    }

    private Map<String, String> gameRules;
    public Map<String, String> getGameRules() {
        return gameRules;
    }

    private ICachedGeneratorType generatorType;
    public ICachedGeneratorType getGeneratorType() {
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
        this(world.getProperties());
        this.loaded = world.isLoaded();
        this.weather = new CachedCatalogType(world.getWeather());
    }
    public CachedWorld(WorldProperties world) {
        super(world);

        this.uuid = UUID.fromString(world.getUniqueId().toString());
        this.name = world.getWorldName();
        this.loaded = false;
        this.loadOnStartup = world.loadOnStartup();
        this.keepSpawnLoaded = world.doesKeepSpawnLoaded();
        this.allowCommands = world.areCommandsAllowed();
        this.generateBonusChests = world.doesGenerateBonusChest();
        this.mapFeaturesEnabled = world.usesMapFeatures();
        this.border = new CachedWorldBorder(world);
        this.difficulty = new CachedCatalogType(world.getDifficulty());
        this.dimensionType = new CachedCatalogType(world.getDimensionType());
        this.gameMode = new CachedCatalogType(world.getGameMode());
        this.gameRules = new HashMap<>(world.getGameRules());
        this.generatorType = new CachedGeneratorType(world.getGeneratorType());
        this.seed = world.getSeed();
        this.spawn = world.getSpawnPosition();
        this.time = world.getWorldTime();
    }

    @Override
    public Optional<?> getLive() {
        if (loaded) {
            return Sponge.getServer().getWorld(uuid);
        } else {
            return Sponge.getServer().getWorldProperties(uuid);
        }
    }

    @Override
    public String getLink() {
        return "/api/world/" + uuid;
    }
}
