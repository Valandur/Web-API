package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.SnowedData;
import valandur.webapi.cache.CachedObject;

public class CachedSnowedData extends CachedObject<SnowedData> {

    @JsonValue
    public boolean snow;


    public CachedSnowedData(SnowedData value) {
        super(value);

        this.snow = value.hasSnow().get();
    }
}
