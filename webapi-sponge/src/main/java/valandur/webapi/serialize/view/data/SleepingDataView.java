package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SleepingData;
import valandur.webapi.serialize.BaseView;

public class SleepingDataView extends BaseView<SleepingData> {

    @JsonValue
    public boolean sleeping;


    public SleepingDataView(SleepingData value) {
        super(value);

        this.sleeping = value.sleeping().get();
    }
}
