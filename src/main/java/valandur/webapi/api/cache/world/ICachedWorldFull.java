package valandur.webapi.api.cache.world;

import com.flowpowered.math.vector.Vector3i;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.weather.Weather;
import valandur.webapi.api.cache.misc.ICachedCatalogType;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.Map;

@ApiModel(value = "WorldFull", parent = ICachedWorld.class)
public interface ICachedWorldFull extends ICachedWorld {

    @JsonDetails
    @ApiModelProperty(value = "True if this world is loaded when the server starts, false otherwise", required = true)
    boolean isLoadOnStartup();

    @JsonDetails
    @ApiModelProperty(value = "True if the spawn of this world is always kept loaded, false otherwise", required = true)
    boolean isKeepSpawnLoaded();

    @JsonDetails
    @ApiModelProperty(value = "True if commands are allowed to be executed in this world, false otherwise", required = true)
    boolean isAllowCommands();

    @JsonDetails
    @ApiModelProperty(value = "True if bonus chests are generated for this world, false otherwise", required = true)
    boolean isGenerateBonusChests();

    @JsonDetails
    @ApiModelProperty(value = "True if map specific features are enabled for this world, false otherwise", required = true)
    boolean isMapFeaturesEnabled();

    @JsonDetails
    @ApiModelProperty(value = "The border of the world", required = true)
    ICachedWorldBorder getBorder();

    @JsonDetails
    @ApiModelProperty(value = "The difficulty of the world", required = true)
    ICachedCatalogType<Difficulty> getDifficulty();

    @JsonDetails
    @ApiModelProperty(value = "The dimension of the world", required = true)
    ICachedCatalogType<DimensionType> getDimensionType();

    @JsonDetails
    @ApiModelProperty(value = "The game mode of the world", required = true)
    ICachedCatalogType<GameMode> getGameMode();

    @JsonDetails
    @ApiModelProperty(value = "A map of world rule names to values", required = true)
    Map<String, String> getGameRules();

    @JsonDetails
    @ApiModelProperty(value = "The generator type used for this world", required = true)
    ICachedCatalogType<GeneratorType> getGeneratorType();

    @JsonDetails
    @ApiModelProperty(value = "The seed of the world", required = true)
    long getSeed();

    @JsonDetails
    @ApiModelProperty(value = "The spawn point for new players", required = true)
    Vector3i getSpawn();

    @JsonDetails
    @ApiModelProperty(value = "The current time in the world", required = true)
    long getTime();

    @JsonDetails
    @ApiModelProperty(value = "The current weather in the world", required = true)
    ICachedCatalogType<Weather> getWeather();
}
