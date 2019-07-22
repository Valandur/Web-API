package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.PlayerCreatedData;
import valandur.webapi.serialize.BaseView;

public class PlayerCreatedDataView extends BaseView<PlayerCreatedData> {

    @JsonValue
    public boolean created;


    public PlayerCreatedDataView(PlayerCreatedData value) {
        super(value);

        this.created = value.playerCreated().get();
    }
}
