package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SittingData;
import valandur.webapi.serialize.BaseView;

public class SittingDataView extends BaseView<SittingData> {

    @JsonValue
    public boolean sitting;


    public SittingDataView(SittingData value) {
        super(value);

        this.sitting = value.sitting().get();
    }
}
