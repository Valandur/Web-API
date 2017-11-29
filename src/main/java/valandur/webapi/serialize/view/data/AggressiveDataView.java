package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.AggressiveData;
import valandur.webapi.api.serialize.BaseView;

public class AggressiveDataView extends BaseView<AggressiveData> {

    @JsonValue
    public boolean aggressive;


    public AggressiveDataView(AggressiveData value) {
        super(value);

        this.aggressive = value.aggressive().get();
    }
}
