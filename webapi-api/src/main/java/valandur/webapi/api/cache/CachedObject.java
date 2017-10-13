package valandur.webapi.api.cache;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.DataManipulator;
import valandur.webapi.api.WebAPIAPI;
import valandur.webapi.api.serialize.ISerializeService;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class CachedObject<T> implements ICachedObject<T> {
    protected long cachedAt;
    protected long cacheDuration = 0;

    protected ICacheService cacheService;
    protected ISerializeService jsonService;
    protected Class<?> clazz;

    protected Map<String, Object> data;
    @Override
    @JsonDetails
    @JsonAnyGetter
    public Map<String, Object> getData() {
        return data;
    }


    public CachedObject(T value) {
        this.cachedAt = System.nanoTime();
        this.cacheService = WebAPIAPI.getCacheService().orElse(null);
        this.jsonService = WebAPIAPI.getJsonService().orElse(null);

        this.cacheDuration = cacheService.getCacheDurationFor(this.getClass());

        if (value != null) this.clazz = value.getClass();

        if (value instanceof DataHolder) {
            DataHolder holder = (DataHolder)value;

            this.data = new HashMap<>();
            Map<String, Class<? extends DataManipulator>> supData = jsonService.getSupportedData();
            for (Map.Entry<String, Class<? extends DataManipulator>> entry : supData.entrySet()) {
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

    @JsonIgnore
    @Override
    public Optional<T> getLive() {
        return Optional.empty();
    }

    @JsonIgnore
    @Override
    public final boolean isExpired() {
        return (System.nanoTime() - cachedAt) / 1000000000 > cacheDuration;
    }
}
