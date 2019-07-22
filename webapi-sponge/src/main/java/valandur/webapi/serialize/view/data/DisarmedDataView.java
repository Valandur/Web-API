package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.DisarmedData;
import valandur.webapi.serialize.BaseView;

public class DisarmedDataView extends BaseView<DisarmedData> {

    @JsonValue
    public boolean disarmed;


    public DisarmedDataView(DisarmedData value) {
        super(value);

        this.disarmed = value.disarmed().get();
    }
}
