package valandur.webapi.cache;

import com.google.gson.JsonElement;

import java.util.Optional;

public abstract class CachedObject {

    protected transient boolean details = false;
    protected transient long cachedAt;

    public abstract int getCacheDuration();
    public abstract Optional<Object> getLive();

    public CachedObject() {
        this.cachedAt = System.nanoTime();
    }

    public final boolean isExpired() {
        return (System.nanoTime() - cachedAt) / 1000000000 > getCacheDuration();
    }
    public final boolean hasDetails() {
        return details;
    }
}
