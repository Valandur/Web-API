package valandur.webapi.cache.player;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.util.RespawnLocation;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.world.CachedLocation;

public class CachedRespawnLocation extends CachedObject<RespawnLocation> {

    @JsonValue
    public CachedLocation loc;


    public CachedRespawnLocation(RespawnLocation value) {
        super(value);

        value.asLocation().ifPresent(l -> loc = new CachedLocation(l));
    }
}
