package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ExplosionRadiusData;
import valandur.webapi.api.serialize.BaseView;

public class ExplosionRadiusDataView extends BaseView<ExplosionRadiusData> {

    @JsonValue
    public Integer radius;


    public ExplosionRadiusDataView(ExplosionRadiusData value) {
        super(value);

        this.radius = value.explosionRadius().get().orElse(null);
    }
}
