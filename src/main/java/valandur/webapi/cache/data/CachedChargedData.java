package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ChargedData;
import valandur.webapi.cache.CachedObject;

public class CachedChargedData extends CachedObject<ChargedData> {

    @JsonValue
    public boolean charged;


    public CachedChargedData(ChargedData value) {
        super(value);

        this.charged = value.charged().get();
    }
}
