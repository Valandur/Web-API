package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData;
import valandur.webapi.api.serialize.BaseView;

public class SneakingDataView extends BaseView<SneakingData> {

    @JsonValue
    public boolean sneaking;


    public SneakingDataView(SneakingData value) {
        super(value);

        this.sneaking = value.sneaking().get();
    }
}
