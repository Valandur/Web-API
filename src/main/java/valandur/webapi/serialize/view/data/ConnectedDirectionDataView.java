package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.ConnectedDirectionData;
import org.spongepowered.api.util.Direction;
import valandur.webapi.api.serialize.BaseView;

import java.util.ArrayList;
import java.util.List;

public class ConnectedDirectionDataView extends BaseView<ConnectedDirectionData> {

    @JsonValue
    public List<Direction> directions;


    public ConnectedDirectionDataView(ConnectedDirectionData value) {
        super(value);

        this.directions = new ArrayList<>(value.connectedDirections().get());
    }
}
