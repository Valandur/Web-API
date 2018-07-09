package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ExplosionRadiusData;
import valandur.webapi.cache.CachedObject;

public class CachedExplosionRadiusData extends CachedObject<ExplosionRadiusData> {

    @JsonValue
    public Integer radius;


    public CachedExplosionRadiusData(ExplosionRadiusData value) {
        super(value);

        this.radius = value.explosionRadius().get().orElse(null);
    }
}
