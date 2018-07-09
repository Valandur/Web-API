package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.KnockbackData;
import valandur.webapi.cache.CachedObject;

public class CachedKnockbackData extends CachedObject<KnockbackData> {

    @JsonValue
    public int knockback;


    public CachedKnockbackData(KnockbackData value) {
        super(value);

        this.knockback = value.knockbackStrength().get();
    }
}
