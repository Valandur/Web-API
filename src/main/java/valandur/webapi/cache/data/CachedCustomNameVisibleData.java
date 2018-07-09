package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.CustomNameVisibleData;
import valandur.webapi.cache.CachedObject;

public class CachedCustomNameVisibleData extends CachedObject<CustomNameVisibleData> {

    @JsonValue
    public boolean visible;


    public CachedCustomNameVisibleData(CustomNameVisibleData value) {
        super(value);

        this.visible = value.customNameVisible().get();
    }
}
