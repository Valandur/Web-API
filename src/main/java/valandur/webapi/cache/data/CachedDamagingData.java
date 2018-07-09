package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.DamagingData;
import valandur.webapi.cache.CachedObject;

public class CachedDamagingData extends CachedObject<DamagingData> {

    @JsonValue
    public double damage;

    public CachedDamagingData(DamagingData value) {
        super(value);

        this.damage = value.damage().get();
    }
}
