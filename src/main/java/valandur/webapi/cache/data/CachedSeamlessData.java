package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.SeamlessData;
import valandur.webapi.cache.CachedObject;

public class CachedSeamlessData extends CachedObject<SeamlessData> {

    @JsonValue
    public boolean seamless;


    public CachedSeamlessData(SeamlessData value) {
        super(value);

        this.seamless = value.seamless().get();
    }
}
