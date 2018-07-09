package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.OccupiedData;
import valandur.webapi.cache.CachedObject;

public class CachedOccupiedData extends CachedObject<OccupiedData> {

    @JsonValue
    public boolean occupied;


    public CachedOccupiedData(OccupiedData value) {
        super(value);

        this.occupied = value.occupied().get();
    }
}
