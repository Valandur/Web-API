package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.VariantData;
import valandur.webapi.cache.CachedObject;

public class CachedVariantData extends CachedObject<VariantData> {

    @JsonValue
    public Object type;


    public CachedVariantData(VariantData value) {
        super(value);

        this.type = value.type().get();
    }
}
