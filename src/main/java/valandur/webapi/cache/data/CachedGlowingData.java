package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.GlowingData;
import valandur.webapi.cache.CachedObject;

public class CachedGlowingData extends CachedObject<GlowingData> {

    @JsonValue
    public boolean glowing;


    public CachedGlowingData(GlowingData value) {
        super(value);

        this.glowing = value.glowing().get();
    }
}
