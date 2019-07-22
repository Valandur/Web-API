package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.DelayableData;
import valandur.webapi.serialize.BaseView;

public class DelayableDataView extends BaseView<DelayableData> {

    @JsonValue
    public int delay;


    public DelayableDataView(DelayableData value) {
        super(value);

        this.delay = value.delay().get();
    }
}
