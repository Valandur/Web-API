package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.DropData;
import valandur.webapi.cache.CachedObject;

public class CachedDropData extends CachedObject<DropData> {

    @JsonValue
    public boolean drops;


    public CachedDropData(DropData value) {
        super(value);

        this.drops = value.willDrop().get();
    }
}
