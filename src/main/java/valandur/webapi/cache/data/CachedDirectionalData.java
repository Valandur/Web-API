package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.DirectionalData;
import org.spongepowered.api.util.Direction;
import valandur.webapi.cache.CachedObject;

public class CachedDirectionalData extends CachedObject<DirectionalData> {

    @JsonValue
    public Direction direction;


    public CachedDirectionalData(DirectionalData value) {
        super(value);

        this.direction = value.direction().get();
    }
}
