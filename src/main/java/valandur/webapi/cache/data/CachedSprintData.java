package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SprintData;
import valandur.webapi.cache.CachedObject;

public class CachedSprintData extends CachedObject<SprintData> {

    @JsonValue
    public boolean spriting;


    public CachedSprintData(SprintData value) {
        super(value);

        this.spriting = value.sprinting().get();
    }
}
