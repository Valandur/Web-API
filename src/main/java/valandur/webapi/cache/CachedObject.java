package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class CachedObject {

    protected transient boolean details = false;
    protected transient long cachedAt;

    @JsonIgnore
    public abstract int getCacheDuration();
    @JsonIgnore
    public abstract Optional<Object> getLive();

    public CachedObject() {
        this.cachedAt = System.nanoTime();
    }

    @JsonIgnore
    public final boolean isExpired() {
        return (System.nanoTime() - cachedAt) / 1000000000 > getCacheDuration();
    }
    @JsonIgnore
    public final boolean hasDetails() {
        return details;
    }

    public String getLink() {
        return null;
    }
}
