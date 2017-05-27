package valandur.webapi.cache;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.item.inventory.Inventory;

import java.util.Optional;

public abstract class CachedObject {
    protected long cachedAt;

    protected Class clazz;
    public Class getObjectClass() {
        return clazz;
    }

    protected DataHolder data;
    public DataHolder getData() {
        return data;
    }


    public CachedObject(Object obj) {
        this.cachedAt = System.nanoTime();

        if (obj != null) this.clazz = obj.getClass();

        if (obj instanceof DataHolder) {
            this.data = ((DataHolder)obj).copy();
        }
    }

    public String getLink() {
        return null;
    }

    public int getCacheDuration() {
        return Integer.MAX_VALUE;
    }
    public Optional<?> getLive() {
        return Optional.empty();
    }
    public final boolean isExpired() {
        return (System.nanoTime() - cachedAt) / 1000000000 > getCacheDuration();
    }
}
