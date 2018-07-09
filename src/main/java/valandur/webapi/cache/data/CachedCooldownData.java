package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.tileentity.CooldownData;
import valandur.webapi.cache.CachedObject;

public class CachedCooldownData extends CachedObject<CooldownData> {

    @JsonValue
    public int cooldown;


    public CachedCooldownData(CooldownData value) {
        super(value);

        this.cooldown = value.cooldown().get();
    }
}
