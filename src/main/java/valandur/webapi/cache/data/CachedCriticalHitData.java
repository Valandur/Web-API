package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.CriticalHitData;
import valandur.webapi.cache.CachedObject;

public class CachedCriticalHitData extends CachedObject<CriticalHitData> {

    @JsonValue
    public boolean criticalHit;


    public CachedCriticalHitData(CriticalHitData value) {
        super(value);

        this.criticalHit = value.criticalHit().get();
    }
}
