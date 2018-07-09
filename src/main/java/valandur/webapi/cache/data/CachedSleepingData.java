package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SleepingData;
import valandur.webapi.cache.CachedObject;

public class CachedSleepingData extends CachedObject<SleepingData> {

    @JsonValue
    public boolean sleeping;


    public CachedSleepingData(SleepingData value) {
        super(value);

        this.sleeping = value.sleeping().get();
    }
}
