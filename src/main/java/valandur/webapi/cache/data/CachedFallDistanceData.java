package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.FallDistanceData;
import valandur.webapi.cache.CachedObject;

public class CachedFallDistanceData extends CachedObject<FallDistanceData> {

    @JsonValue
    public float fallDistance;


    public CachedFallDistanceData(FallDistanceData value) {
        super(value);

        this.fallDistance = value.fallDistance().get();
    }
}
