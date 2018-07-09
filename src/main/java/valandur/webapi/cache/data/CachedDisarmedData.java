package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.DisarmedData;
import valandur.webapi.cache.CachedObject;

public class CachedDisarmedData extends CachedObject<DisarmedData> {

    @JsonValue
    public boolean disarmed;


    public CachedDisarmedData(DisarmedData value) {
        super(value);

        this.disarmed = value.disarmed().get();
    }
}
