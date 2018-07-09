package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.PigSaddleData;
import valandur.webapi.cache.CachedObject;

public class CachedPigSaddleData extends CachedObject<PigSaddleData> {

    @JsonValue
    public boolean saddle;


    public CachedPigSaddleData(PigSaddleData value) {
        super(value);

        this.saddle = value.saddle().get();
    }
}
