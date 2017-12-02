package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.tileentity.CooldownData;
import valandur.webapi.api.serialize.BaseView;

public class CooldownDataView extends BaseView<CooldownData> {

    @JsonValue
    public int cooldown;


    public CooldownDataView(CooldownData value) {
        super(value);

        this.cooldown = value.cooldown().get();
    }
}
