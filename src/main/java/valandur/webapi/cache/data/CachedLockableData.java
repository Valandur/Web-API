package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.tileentity.LockableData;
import valandur.webapi.cache.CachedObject;

public class CachedLockableData extends CachedObject<LockableData> {

    @JsonValue
    public String lock;


    public CachedLockableData(LockableData value) {
        super(value);

        this.lock = value.lockToken().get();
    }
}
