package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ScreamingData;
import valandur.webapi.cache.CachedObject;

public class CachedScreamingData extends CachedObject<ScreamingData> {

    @JsonValue
    public boolean screaming;


    public CachedScreamingData(ScreamingData value) {
        super(value);

        this.screaming = value.screaming().get();
    }
}
