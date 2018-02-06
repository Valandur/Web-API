package valandur.webapi.cache.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.weather.Weather;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.api.cache.misc.CachedCatalogType;
import valandur.webapi.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CachedWorld extends CachedObject<World> implements ICachedWorld {

    private UUID uuid;
    @Override
    public UUID getUUID() {
        return uuid;
    }

    private String name;
    @Override
    public String getName() {
        return name;
    }

    private boolean loaded;
    @Override
    public boolean isLoaded() {
        return loaded;
    }

    private boolean loadOnStartup;
    @JsonDetails
    public boolean isLoadOnStartup() {
        return loadOnStartup;
    }

    private boolean keepSpawnLoaded;
    @JsonDetails
    public boolean isKeepSpawnLoaded() {
        return keepSpawnLoaded;
    }

    private boolean allowCommands;
    @JsonDetails
    public boolean isAllowCommands() {
        return allowCommands;
    }

    private boolean generateBonusChests;
    @JsonDetails
    public boolean isGenerateBonusChests() {
        return generateBonusChests;
    }

    private boolean mapFeaturesEnabled;
    @JsonDetails
    public boolean isMapFeaturesEnabled() {
        return mapFeaturesEnabled;
    }

    private CachedWorldBorder border;
    @JsonDetails
    public CachedWorldBorder getBorder() {
        return border;
    }

    public CachedCatalogType<Difficulty> getDifficulty() {
        return difficulty;
    }
    @JsonDetails
    private CachedCatalogType<Difficulty> difficulty;

    private CachedCatalogType<DimensionType> dimensionType;
    @JsonDetails
    public CachedCatalogType<DimensionType> getDimensionType() {
        return dimensionType;
    }

    private CachedCatalogType<GameMode> gameMode;
    @JsonDetails
    public CachedCatalogType<GameMode> getGameMode() {
        return gameMode;
    }

    private Map<String, String> gameRules;
    @JsonDetails
    public Map<String, String> getGameRules() {
        return gameRules;
    }

    private CachedCatalogType<GeneratorType> generatorType;
    @JsonDetails
    public CachedCatalogType<GeneratorType> getGeneratorType() {
        return generatorType;
    }

    private long seed;
    @JsonDetails
    public long getSeed() {
        return seed;
    }

    private Vector3i spawn;
    @JsonDetails
    public Vector3i getSpawn() {
        return spawn;
    }

    private long time;
    @JsonDetails
    public long getTime() {
        return time;
    }

    private CachedCatalogType<Weather> weather;
    @JsonDetails
    public CachedCatalogType<Weather> getWeather() {
        return weather;
    }


    public CachedWorld(World world) {
        super(world);

        saveWorldProperties(world.getProperties());

        this.loaded = world.isLoaded();
        this.weather = new CachedCatalogType<>(world.getWeather());
    }
    public CachedWorld(WorldProperties world) {
        super(null);

        saveWorldProperties(world);
    }
    private void saveWorldProperties(WorldProperties props) {
        this.uuid = UUID.fromString(props.getUniqueId().toString());
        this.name = props.getWorldName();
        this.loaded = false;

        this.loadOnStartup = props.loadOnStartup();
        this.keepSpawnLoaded = props.doesKeepSpawnLoaded();
        this.allowCommands = props.areCommandsAllowed();
        this.generateBonusChests = props.doesGenerateBonusChest();
        this.mapFeaturesEnabled = props.usesMapFeatures();
        this.border = new CachedWorldBorder(props);
        this.difficulty = new CachedCatalogType<>(props.getDifficulty());
        this.dimensionType = new CachedCatalogType<>(props.getDimensionType());
        this.gameMode = new CachedCatalogType<>(props.getGameMode());
        this.gameRules = new HashMap<>(props.getGameRules());
        this.generatorType = new CachedCatalogType<>(props.getGeneratorType());
        this.seed = props.getSeed();
        this.spawn = props.getSpawnPosition();
        this.time = props.getWorldTime();
    }

    @Override
    public Optional<World> getLive() {
        return Sponge.getServer().getWorld(uuid);
    }
    @JsonIgnore
    @Override
    public Optional<WorldProperties> getLiveProps() {
        return Sponge.getServer().getWorldProperties(uuid);
    }

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/world/" + uuid;
    }
}
