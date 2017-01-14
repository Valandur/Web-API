package valandur.webapi.cache;

import com.google.gson.JsonElement;

public abstract class CachedObject {

    protected transient boolean details = false;
    protected transient long cachedAt;

    protected JsonElement raw;

    public abstract int getCacheDuration();

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
