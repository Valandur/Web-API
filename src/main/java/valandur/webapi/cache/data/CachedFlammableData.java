package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.FlammableData;
import valandur.webapi.cache.CachedObject;

public class CachedFlammableData extends CachedObject<FlammableData> {

    @JsonValue
    public boolean flammable;


    public CachedFlammableData(FlammableData value) {
        super(value);

        this.flammable = value.flammable().get();
    }
}
