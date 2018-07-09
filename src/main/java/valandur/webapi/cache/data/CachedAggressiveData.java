package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.AggressiveData;
import valandur.webapi.cache.CachedObject;

public class CachedAggressiveData extends CachedObject<AggressiveData> {

    @JsonValue
    public boolean aggressive;


    public CachedAggressiveData(AggressiveData value) {
        super(value);

        this.aggressive = value.aggressive().get();
    }
}
