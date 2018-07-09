package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingData;
import valandur.webapi.cache.CachedObject;

public class CachedFlyingData extends CachedObject<FlyingData> {

    @JsonValue
    public boolean flying;


    public CachedFlyingData(FlyingData value) {
        super(value);

        this.flying = value.flying().get();
    }
}
