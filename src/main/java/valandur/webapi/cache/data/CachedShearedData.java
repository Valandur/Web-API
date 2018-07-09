package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ShearedData;
import valandur.webapi.cache.CachedObject;

public class CachedShearedData extends CachedObject<ShearedData> {

    @JsonValue
    public boolean sheared;


    public CachedShearedData(ShearedData value) {
        super(value);

        this.sheared = value.sheared().get();
    }
}
