package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.RepresentedPlayerData;
import valandur.webapi.cache.CachedObject;

public class CachedRepresentedPlayerData extends CachedObject<RepresentedPlayerData> {

    @JsonValue
    public String owner;


    public CachedRepresentedPlayerData(RepresentedPlayerData value) {
        super(value);

        this.owner = value.owner().get().getName().orElse(null);
    }
}
