package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.AbsorptionData;
import valandur.webapi.cache.CachedObject;

public class CachedAbsorptionData extends CachedObject<AbsorptionData> {

    @JsonValue
    public double abs;


    public CachedAbsorptionData(AbsorptionData value) {
        super(value);

        this.abs = value.absorption().get();
    }
}
