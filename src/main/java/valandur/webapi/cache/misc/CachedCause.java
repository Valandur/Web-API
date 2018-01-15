package valandur.webapi.cache.misc;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.event.cause.Cause;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.util.Util;

import java.util.Map;

public class CachedCause extends CachedObject<Cause> {

    @Override
    @JsonValue
    @JsonDetails(false)
    public Map<String, Object> getData() {
        return super.getData();
    }

    public CachedCause(Cause cause) {
        super(cause);

        for (Map.Entry<String, Object> entry : cause.getNamedCauses().entrySet()) {
            String key = Util.lowerFirst(entry.getKey());
            data.put(key, WebAPI.getCacheService().asCachedObject(entry.getValue()));
        }
    }
}
