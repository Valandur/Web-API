package valandur.webapi.cache.misc;

import org.spongepowered.api.event.cause.Cause;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.util.Util;

import java.util.HashMap;
import java.util.Map;

public class CachedCause extends CachedObject<Cause> {

    private Map<String, Object> causes = new HashMap<>();
    public Map<String, Object> getCauses() {
        return causes;
    }


    public CachedCause(Cause cause) {
        super(cause);

        for (Map.Entry<String, Object> entry : cause.getNamedCauses().entrySet()) {
            String key = Util.lowerFirst(entry.getKey());
            causes.put(key, WebAPI.getCacheService().asCachedObject(entry.getValue()));
        }
    }
}
