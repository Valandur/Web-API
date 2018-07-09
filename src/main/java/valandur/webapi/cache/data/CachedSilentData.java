package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SilentData;
import valandur.webapi.cache.CachedObject;

public class CachedSilentData extends CachedObject<SilentData> {

    @JsonValue
    public boolean silent;


    public CachedSilentData(SilentData value) {
        super(value);

        this.silent = value.silent().get();
    }
}
