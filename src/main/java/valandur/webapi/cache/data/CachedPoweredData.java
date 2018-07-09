package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.PoweredData;
import valandur.webapi.cache.CachedObject;

public class CachedPoweredData extends CachedObject<PoweredData> {

    @JsonValue
    public boolean powered;


    public CachedPoweredData(PoweredData value) {
        super(value);

        this.powered = value.powered().get();
    }
}
