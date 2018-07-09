package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SittingData;
import valandur.webapi.cache.CachedObject;

public class CachedSittingData extends CachedObject<SittingData> {

    @JsonValue
    public boolean sitting;


    public CachedSittingData(SittingData value) {
        super(value);

        this.sitting = value.sitting().get();
    }
}
