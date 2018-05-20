package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.DamagingData;
import valandur.webapi.serialize.BaseView;

public class DamagingDataView extends BaseView<DamagingData> {

    @JsonValue
    public double damage;

    public DamagingDataView(DamagingData value) {
        super(value);

        this.damage = value.damage().get();
    }
}
