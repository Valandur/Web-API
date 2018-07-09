package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.OpenData;
import valandur.webapi.cache.CachedObject;

public class CachedOpenData extends CachedObject<OpenData> {

    @JsonValue
    public boolean open;


    public CachedOpenData(OpenData value) {
        super(value);

        this.open = value.open().get();
    }
}
