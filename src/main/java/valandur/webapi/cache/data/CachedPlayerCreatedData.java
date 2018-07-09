package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.PlayerCreatedData;
import valandur.webapi.cache.CachedObject;

public class CachedPlayerCreatedData extends CachedObject<PlayerCreatedData> {

    @JsonValue
    public boolean created;


    public CachedPlayerCreatedData(PlayerCreatedData value) {
        super(value);

        this.created = value.playerCreated().get();
    }
}
