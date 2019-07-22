package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SilentData;
import valandur.webapi.serialize.BaseView;

public class SilentDataView extends BaseView<SilentData> {

    @JsonValue
    public boolean silent;


    public SilentDataView(SilentData value) {
        super(value);

        this.silent = value.silent().get();
    }
}
