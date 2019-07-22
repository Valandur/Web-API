package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import valandur.webapi.serialize.BaseView;

public class RepresentedPlayerDataView extends BaseView<RepresentedPlayerData> {

    @JsonValue
    public String owner;


    public RepresentedPlayerDataView(RepresentedPlayerData value) {
        super(value);

        this.owner = value.owner().get().getName().orElse(null);
    }
}
