package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.DirectionalData;
import org.spongepowered.api.util.Direction;
import valandur.webapi.api.serialize.BaseView;

public class DirectionalDataView extends BaseView<DirectionalData> {

    @JsonValue
    public Direction direction;


    public DirectionalDataView(DirectionalData value) {
        super(value);

        this.direction = value.direction().get();
    }
}
