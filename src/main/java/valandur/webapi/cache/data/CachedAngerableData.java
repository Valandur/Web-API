package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.AngerableData;
import valandur.webapi.cache.CachedObject;

public class CachedAngerableData extends CachedObject<AngerableData> {

    @JsonValue
    public int anger;


    public CachedAngerableData(AngerableData value) {
        super(value);

        this.anger = value.angerLevel().get();
    }
}
