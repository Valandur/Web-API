package valandur.webapi.cache;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.DataManipulator;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.ICachedObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class CachedObject implements ICachedObject {
    protected long cachedAt;
    protected long cacheDuration = 0;

    protected Class clazz;
    @Override
    public Class getObjectClass() {
        return clazz;
    }

    protected Map<String, Object> data;
    @Override
    public Map<String, Object> getData() {
        return data;
    }


    public CachedObject(Object obj) {
        this.cachedAt = System.nanoTime();
        this.cacheDuration = WebAPI.getCacheService().getCacheDurationFor(this.getClass());

        if (obj != null) this.clazz = obj.getClass();

        if (obj instanceof DataHolder) {
            DataHolder holder = (DataHolder)obj;

            this.data = new HashMap<>();
            for (Map.Entry<String, Class<? extends DataManipulator>> entry :
                    WebAPI.getJsonService().getSupportedData().entrySet()) {
                Optional<?> m = holder.get(entry.getValue());

                if (!m.isPresent())
                    continue;

                data.put(entry.getKey(), ((DataManipulator)m.get()).copy());
            }
        }
    }

    @Override
    public String getLink() {
        return null;
    }

    @Override
    public Optional<?> getLive() {
        return Optional.empty();
    }
    @Override
    public final boolean isExpired() {
        return (System.nanoTime() - cachedAt) / 1000000000 > cacheDuration;
    }
}
