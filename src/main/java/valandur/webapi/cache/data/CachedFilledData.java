package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.FilledData;
import valandur.webapi.cache.CachedObject;

public class CachedFilledData extends CachedObject<FilledData> {

    @JsonValue
    public boolean filled;


    public CachedFilledData(FilledData value) {
        super(value);

        this.filled = value.filled().get();
    }
}
