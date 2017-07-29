package valandur.webapi.api.cache.world;

import com.flowpowered.math.vector.Vector3i;
import valandur.webapi.api.cache.ICachedObject;

import java.util.UUID;

public interface ICachedChunk extends ICachedObject {

    UUID getUUID();

    Vector3i getPosition();

    ICachedWorld getWorld();

    Vector3i getBlockMin();

    Vector3i getBlockMax();

    boolean isLoaded();

    int getInhabittedTime();

    double getRegionalDifficultyFactor();

    double getRegionalDifficultyPercentage();
}
