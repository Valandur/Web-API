package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ExpOrbData;
import valandur.webapi.api.serialize.BaseView;

public class ExpOrbDataView extends BaseView<ExpOrbData> {

    @JsonValue
    public int exp;


    public ExpOrbDataView(ExpOrbData value) {
        super(value);

        this.exp = value.experience().get();
    }
}
