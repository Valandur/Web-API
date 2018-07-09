package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.RedstonePoweredData;
import valandur.webapi.cache.CachedObject;

public class CachedRedstonePoweredData extends CachedObject<RedstonePoweredData> {

    @JsonValue
    public int power;


    public CachedRedstonePoweredData(RedstonePoweredData value) {
        super(value);

        this.power = value.power().get();
    }
}
