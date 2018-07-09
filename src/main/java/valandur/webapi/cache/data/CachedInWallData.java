package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.InWallData;
import valandur.webapi.cache.CachedObject;

public class CachedInWallData extends CachedObject<InWallData> {

    @JsonValue
    public boolean inWall;


    public CachedInWallData(InWallData value) {
        super(value);

        this.inWall = value.inWall().get();
    }
}
