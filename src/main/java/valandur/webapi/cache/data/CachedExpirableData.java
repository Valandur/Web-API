package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ExpirableData;
import valandur.webapi.cache.CachedObject;

public class CachedExpirableData extends CachedObject<ExpirableData> {

    @JsonValue
    public int ticks;


    public CachedExpirableData(ExpirableData value) {
        super(value);

        this.ticks = value.expireTicks().get();
    }
}
