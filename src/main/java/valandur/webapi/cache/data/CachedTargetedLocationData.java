package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.TargetedLocationData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedVector3d;

public class CachedTargetedLocationData extends CachedObject<TargetedLocationData> {

    @JsonValue
    public CachedVector3d target;


    public CachedTargetedLocationData(TargetedLocationData value) {
        super(value);

        this.target = new CachedVector3d(value.target().get());
    }
}
