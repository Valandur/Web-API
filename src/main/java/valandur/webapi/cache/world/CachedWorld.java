package valandur.webapi.cache.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;
import valandur.webapi.api.cache.world.ICachedWorld;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

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

    public CachedCatalogType getDifficulty() {
        return difficulty;
    }
    @JsonDetails
    private CachedCatalogType difficulty;

    private CachedCatalogType dimensionType;
    @JsonDetails
    public CachedCatalogType getDimensionType() {
        return dimensionType;
    }

    private CachedCatalogType gameMode;
    @JsonDetails
    public CachedCatalogType getGameMode() {
        return gameMode;
    }

    private Map<String, String> gameRules;
    @JsonDetails
    public Map<String, String> getGameRules() {
        return gameRules;
    }

    private CachedGeneratorType generatorType;
    @JsonDetails
    public CachedGeneratorType getGeneratorType() {
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

    private CachedCatalogType weather;
    @JsonDetails
    public CachedCatalogType getWeather() {
        return weather;
    }


    public CachedWorld(World world) {
        super(world);

        saveWorldProperties(world.getProperties());

        this.loaded = world.isLoaded();
        this.weather = new CachedCatalogType(world.getWeather());
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
        //this.keepSpawnLoaded = props.doesKeepSpawnLoaded();
        this.allowCommands = props.areCommandsAllowed();
        this.generateBonusChests = props.doesGenerateBonusChest();
        this.mapFeaturesEnabled = props.usesMapFeatures();
        this.border = new CachedWorldBorder(props);
        this.difficulty = new CachedCatalogType(props.getDifficulty());
        this.dimensionType = new CachedCatalogType(props.getDimensionType());
        this.gameMode = new CachedCatalogType(props.getGameMode());
        this.gameRules = new HashMap<>(props.getGameRules());
        this.generatorType = new CachedGeneratorType(props.getGeneratorType());
        this.seed = props.getSeed();
        this.spawn = props.getSpawnPosition();
        this.time = props.getWorldTime();
    }

    @Override
    public Optional<World> getLive() {
        return Sponge.getServer().getWorld(uuid);
    }
    @JsonIgnore
    public Optional<WorldProperties> getLiveProps() {
        return Sponge.getServer().getWorldProperties(uuid);
    }

    @Override
    public String getLink() {
        return "/api/world/" + uuid;
    }
}
