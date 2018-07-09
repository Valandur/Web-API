package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.ExtendedData;
import valandur.webapi.cache.CachedObject;

public class CachedExtendedData extends CachedObject<ExtendedData> {

    @JsonValue
    public boolean extended;


    public CachedExtendedData(ExtendedData value) {
        super(value);

        this.extended = value.extended().get();
    }
}
