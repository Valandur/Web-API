package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ExpirableData;
import valandur.webapi.serialize.BaseView;

public class ExpirableDataView extends BaseView<ExpirableData> {

    @JsonValue
    public int ticks;


    public ExpirableDataView(ExpirableData value) {
        super(value);

        this.ticks = value.expireTicks().get();
    }
}
