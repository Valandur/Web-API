package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.MoistureData;
import valandur.webapi.cache.CachedObject;

public class CachedMoistureData extends CachedObject<MoistureData> {

    @JsonValue
    public int moisture;


    public CachedMoistureData(MoistureData value) {
        super(value);

        this.moisture = value.moisture().get();
    }
}
