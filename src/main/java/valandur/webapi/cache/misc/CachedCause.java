package valandur.webapi.cache.misc;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContextKey;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedCause extends CachedObject<Cause> {

    private Map<String, Object> context = new HashMap<>();
    public Map<String, Object> getContext() {
        return context;
    }

    private List<Object> causes = new ArrayList<>();
    public List<Object> getCauses() {
        return causes;
    }


    public CachedCause(Cause cause) {
        super(cause);

        for (Map.Entry<EventContextKey<?>, Object> entry : cause.getContext().asMap().entrySet()) {
            String key = Util.lowerFirst(entry.getKey().getId());
            context.put(key, WebAPI.getCacheService().asCachedObject(entry.getValue()));
        }

        for (Object entry : cause.all()) {
            causes.add(WebAPI.getCacheService().asCachedObject(entry));
        }
    }
}
