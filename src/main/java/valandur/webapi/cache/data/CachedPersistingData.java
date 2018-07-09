package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.PersistingData;
import valandur.webapi.cache.CachedObject;

public class CachedPersistingData extends CachedObject<PersistingData> {

    @JsonValue
    public boolean persists;


    public CachedPersistingData(PersistingData value) {
        super(value);

        this.persists = value.persists().get();
    }
}
