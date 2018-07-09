package valandur.webapi.cache.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.weather.Weather;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.misc.CachedVector3i;
import valandur.webapi.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import javax.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApiModel(value = "World")
public class CachedWorld extends CachedObject<World> {

    private UUID uuid;
    @ApiModelProperty(value = "The unique UUID that identifies this world", required = true)
    public UUID getUUID() {
        return uuid;
    }

    private String name;
    @ApiModelProperty(value = "The name of this world", required = true)
    public String getName() {
        return name;
    }

    private boolean loaded;
    @ApiModelProperty(value = "True if the world is loaded, false otherwise", required = true)
    public boolean isLoaded() {
        return loaded;
    }

    private boolean loadOnStartup;
    @JsonDetails
    @ApiModelProperty(value = "True if this world is loaded when the server starts, false otherwise", required = true)
    public boolean isLoadOnStartup() {
        return loadOnStartup;
    }

    private boolean keepSpawnLoaded;
    @JsonDetails
    @ApiModelProperty(value = "True if the spawn of this world is always kept loaded, false otherwise", required = true)
    public boolean isKeepSpawnLoaded() {
        return keepSpawnLoaded;
    }

    private boolean allowCommands;
    @JsonDetails
    @ApiModelProperty(value = "True if commands are allowed to be executed in this world, false otherwise", required = true)
    public boolean isAllowCommands() {
        return allowCommands;
    }

    private boolean generateBonusChests;
    @JsonDetails
    @ApiModelProperty(value = "True if bonus chests are generated for this world, false otherwise", required = true)
    public boolean isGenerateBonusChests() {
        return generateBonusChests;
    }

    private boolean mapFeaturesEnabled;
    @JsonDetails
    @ApiModelProperty(value = "True if map specific features are enabled for this world, false otherwise", required = true)
    public boolean isMapFeaturesEnabled() {
        return mapFeaturesEnabled;
    }

    private CachedWorldBorder border;
    @JsonDetails
    @ApiModelProperty(value = "The border of the world", required = true)
    public CachedWorldBorder getBorder() {
        return border;
    }

    public CachedCatalogType<Difficulty> getDifficulty() {
        return difficulty;
    }
    @JsonDetails
    @ApiModelProperty(value = "The difficulty of the world", required = true)
    private CachedCatalogType<Difficulty> difficulty;

    private CachedCatalogType<DimensionType> dimensionType;
    @JsonDetails
    @ApiModelProperty(value = "The dimension of the world", required = true)
    public CachedCatalogType<DimensionType> getDimensionType() {
        return dimensionType;
    }

    private CachedCatalogType<GameMode> gameMode;
    @JsonDetails
    @ApiModelProperty(value = "The game mode of the world", required = true)
    public CachedCatalogType<GameMode> getGameMode() {
        return gameMode;
    }

    private Map<String, String> gameRules;
    @JsonDetails
    @ApiModelProperty(value = "A map of world rule names to values", required = true)
    public Map<String, String> getGameRules() {
        return gameRules;
    }

    private CachedCatalogType<GeneratorType> generatorType;
    @JsonDetails
    @ApiModelProperty(value = "The generator type used for this world", required = true)
    public CachedCatalogType<GeneratorType> getGeneratorType() {
        return generatorType;
    }

    private long seed;
    @JsonDetails
    @ApiModelProperty(value = "The seed of the world", required = true)
    public long getSeed() {
        return seed;
    }

    private CachedVector3i spawn;
    @JsonDetails
    @ApiModelProperty(value = "The spawn point for new players", required = true)
    public CachedVector3i getSpawn() {
        return spawn;
    }

    private long time;
    @JsonDetails
    @ApiModelProperty(value = "The current time in the world", required = true)
    public long getTime() {
        return time;
    }

    private CachedCatalogType<Weather> weather;
    @JsonDetails
    @ApiModelProperty(value = "The current weather in the world", required = true)
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
        this.spawn = new CachedVector3i(props.getSpawnPosition());
        this.time = props.getWorldTime();
    }

    @Override
    public World getLive() {
        Optional<World> optWorld = Sponge.getServer().getWorld(uuid);
        if (!optWorld.isPresent()) {
            throw new NotFoundException("Could not find world: " + uuid);
        }
        return optWorld.get();
    }

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public WorldProperties getLiveProps() {
        Optional<WorldProperties> optWorldProps = Sponge.getServer().getWorldProperties(uuid);
        if (!optWorldProps.isPresent()) {
            throw new NotFoundException("Could not find world properties: " + uuid);
        }
        return optWorldProps.get();
    }

    @Override
    @JsonIgnore(false)
    public String getLink() {
        return Constants.BASE_PATH + "/world/" + uuid;
    }
}
