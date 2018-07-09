package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.FlyingAbilityData;
import valandur.webapi.cache.CachedObject;

public class CachedFlyingAbilityData extends CachedObject<FlyingAbilityData> {

    @JsonValue
    public boolean canFly;


    public CachedFlyingAbilityData(FlyingAbilityData value) {
        super(value);

        this.canFly = value.canFly().get();
    }
}
