package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.AttachedData;
import valandur.webapi.cache.CachedObject;

public class CachedAttachedData extends CachedObject<AttachedData> {

    @JsonValue
    public boolean attached;


    public CachedAttachedData(AttachedData value) {
        super(value);

        this.attached = value.attached().get();
    }
}
