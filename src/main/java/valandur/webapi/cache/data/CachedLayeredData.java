package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.LayeredData;
import valandur.webapi.cache.CachedObject;

public class CachedLayeredData extends CachedObject<LayeredData> {

    @JsonValue
    public int layer;


    public CachedLayeredData(LayeredData value) {
        super(value);

        this.layer = value.layer().get();
    }
}
