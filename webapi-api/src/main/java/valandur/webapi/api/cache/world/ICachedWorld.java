package valandur.webapi.api.cache.world;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.api.world.weather.Weather;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedCatalogType;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApiModel("World")
public interface ICachedWorld extends ICachedObject<World> {

    @ApiModelProperty(value = "The unique UUID that identifies this world", required = true)
    UUID getUUID();

    @ApiModelProperty(value = "The name of this world", required = true)
    String getName();

    @ApiModelProperty(value = "True if the world is loaded, false otherwise", required = true)
    boolean isLoaded();

    @JsonDetails
    @ApiModelProperty("True if this world is loaded when the server starts, false otherwise")
    boolean isLoadOnStartup();

    @JsonDetails
    @ApiModelProperty("True if the spawn of this world is always kept loaded, false otherwise")
    boolean isKeepSpawnLoaded();

    @JsonDetails
    @ApiModelProperty("True if commands are allowed to be executed in this world, false otherwise")
    boolean isAllowCommands();

    @JsonDetails
    @ApiModelProperty("True if bonus chests are generated for this world, false otherwise")
    boolean isGenerateBonusChests();

    @JsonDetails
    @ApiModelProperty("True if map specific features are enabled for this world, false otherwise")
    boolean isMapFeaturesEnabled();

    @JsonDetails
    @ApiModelProperty("The border of the world")
    ICachedWorldBorder getBorder();

    @JsonDetails
    @ApiModelProperty("The difficulty of the world")
    ICachedCatalogType<Difficulty> getDifficulty();

    @JsonDetails
    @ApiModelProperty("The dimension of the world")
    ICachedCatalogType<DimensionType> getDimensionType();

    @JsonDetails
    @ApiModelProperty("The game mode of the world")
    ICachedCatalogType<GameMode> getGameMode();

    @JsonDetails
    @ApiModelProperty("A map of world rule names to values")
    Map<String, String> getGameRules();

    @JsonDetails
    @ApiModelProperty("The generator type used for this world")
    ICachedCatalogType<GeneratorType> getGeneratorType();

    @JsonDetails
    @ApiModelProperty("The seed of the world")
    long getSeed();

    @JsonDetails
    @ApiModelProperty("The spawn point for new players")
    Vector3i getSpawn();

    @JsonDetails
    @ApiModelProperty("The current time in the world")
    long getTime();

    @JsonDetails
    @ApiModelProperty("The current weather in the world")
    ICachedCatalogType<Weather> getWeather();

    @ApiModelProperty(hidden = true)
    Optional<WorldProperties> getLiveProps();
}
