package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.DelayableData;
import valandur.webapi.cache.CachedObject;

public class CachedDelayableData extends CachedObject<DelayableData> {

    @JsonValue
    public int delay;


    public CachedDelayableData(DelayableData value) {
        super(value);

        this.delay = value.delay().get();
    }
}
