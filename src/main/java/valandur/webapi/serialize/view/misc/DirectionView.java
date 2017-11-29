package valandur.webapi.serialize.view.misc;

import com.fasterxml.jackson.annotation.JsonValue;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.util.Direction;
import valandur.webapi.api.serialize.BaseView;

public class DirectionView extends BaseView<Direction> {

    @JsonValue
    public Vector3d dir;


    public DirectionView(Direction value) {
        super(value);

        this.dir = value.asOffset();
    }
}
