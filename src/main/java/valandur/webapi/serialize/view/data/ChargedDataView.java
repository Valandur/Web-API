package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ChargedData;
import valandur.webapi.api.serialize.BaseView;

public class ChargedDataView extends BaseView<ChargedData> {

    @JsonValue
    public boolean charged;


    public ChargedDataView(ChargedData value) {
        super(value);

        this.charged = value.charged().get();
    }
}
