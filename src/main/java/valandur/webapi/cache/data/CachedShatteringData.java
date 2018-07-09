package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ShatteringData;
import valandur.webapi.cache.CachedObject;

public class CachedShatteringData extends CachedObject<ShatteringData> {

    @JsonValue
    public boolean shatters;


    public CachedShatteringData(ShatteringData value) {
        super(value);

        this.shatters = value.willShatter().get();
    }
}
