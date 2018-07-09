package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ExpOrbData;
import valandur.webapi.cache.CachedObject;

public class CachedExpOrbData extends CachedObject<ExpOrbData> {

    @JsonValue
    public int exp;


    public CachedExpOrbData(ExpOrbData value) {
        super(value);

        this.exp = value.experience().get();
    }
}
