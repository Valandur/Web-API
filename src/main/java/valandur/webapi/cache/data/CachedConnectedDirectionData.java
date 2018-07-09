package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.ConnectedDirectionData;
import org.spongepowered.api.util.Direction;
import valandur.webapi.cache.CachedObject;

import java.util.ArrayList;
import java.util.List;

public class CachedConnectedDirectionData extends CachedObject<ConnectedDirectionData> {

    @JsonValue
    public List<Direction> directions;


    public CachedConnectedDirectionData(ConnectedDirectionData value) {
        super(value);

        this.directions = new ArrayList<>(value.connectedDirections().get());
    }
}
