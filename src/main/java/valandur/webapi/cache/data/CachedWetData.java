package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.WetData;
import valandur.webapi.cache.CachedObject;

public class CachedWetData extends CachedObject<WetData> {

    @JsonValue
    public boolean wet;


    public CachedWetData(WetData value) {
        super(value);

        this.wet = value.wet().get();
    }
}
