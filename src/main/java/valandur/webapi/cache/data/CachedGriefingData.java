package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.GriefingData;
import valandur.webapi.cache.CachedObject;

public class CachedGriefingData extends CachedObject<GriefingData> {

    @JsonValue
    public boolean grief;


    public CachedGriefingData(GriefingData value) {
        super(value);

        this.grief = value.canGrief().get();
    }
}
