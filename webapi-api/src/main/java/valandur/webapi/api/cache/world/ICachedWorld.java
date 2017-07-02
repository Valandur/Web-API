package valandur.webapi.api.cache.world;

import com.flowpowered.math.vector.Vector3i;
import valandur.webapi.api.cache.ICachedObject;
import valandur.webapi.api.cache.misc.ICachedCatalogType;

import java.util.Map;
import java.util.UUID;

public interface ICachedWorld extends ICachedObject {

    UUID getUUID();

    String getName();

    boolean isLoaded();

    boolean doesLoadOnStartup();

    boolean doesKeepSpawnLoaded();

    boolean doesAllowCommands();

    boolean doesGenerateBonusChests();

    boolean areMapFeaturesEnabled();

    ICachedWorldBorder getBorder();

    ICachedCatalogType getDifficulty();

    ICachedCatalogType getDimensionType();

    ICachedCatalogType getGameMode();

    Map<String, String> getGameRules();

    ICachedGeneratorType getGeneratorType();

    long getSeed();

    Vector3i getSpawn();

    long getTime();

    ICachedCatalogType getWeather();
}
