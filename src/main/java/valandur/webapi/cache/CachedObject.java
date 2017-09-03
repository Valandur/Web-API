package valandur.webapi.cache;

import org.spongepowered.api.data.DataHolder;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.ICachedObject;

import java.util.Optional;

public abstract class CachedObject implements ICachedObject {
    protected long cachedAt;
    protected long cacheDuration = 0;

    protected Class clazz;
    @Override
    public Class getObjectClass() {
        return clazz;
    }

    protected DataHolder data;
    @Override
    public DataHolder getData() {
        return data;
    }


    public CachedObject(Object obj) {
        this.cachedAt = System.nanoTime();
        this.cacheDuration = WebAPI.getCacheService().getCacheDurationFor(this.getClass());

        if (obj != null) this.clazz = obj.getClass();

        if (obj instanceof DataHolder) {
            this.data = ((DataHolder)obj).copy();
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
