package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData;
import valandur.webapi.cache.CachedObject;

public class CachedSneakingData extends CachedObject<SneakingData> {

    @JsonValue
    public boolean sneaking;


    public CachedSneakingData(SneakingData value) {
        super(value);

        this.sneaking = value.sneaking().get();
    }
}
