package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.StuckArrowsData;
import valandur.webapi.cache.CachedObject;

public class CachedStuckArrowsData extends CachedObject<StuckArrowsData> {

    @JsonValue
    public int stuckArrows;


    public CachedStuckArrowsData(StuckArrowsData value) {
        super(value);

        this.stuckArrows = value.stuckArrows().get();
    }
}
