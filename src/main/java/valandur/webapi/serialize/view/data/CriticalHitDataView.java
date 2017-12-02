package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.CriticalHitData;
import valandur.webapi.api.serialize.BaseView;

public class CriticalHitDataView extends BaseView<CriticalHitData> {

    @JsonValue
    public boolean criticalHit;


    public CriticalHitDataView(CriticalHitData value) {
        super(value);

        this.criticalHit = value.criticalHit().get();
    }
}
