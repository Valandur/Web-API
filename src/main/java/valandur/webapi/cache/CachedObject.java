package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import valandur.webapi.json.JsonConverter;

import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class CachedObject {

    protected transient long cachedAt;

    public CachedObject() {
        this.cachedAt = System.nanoTime();
    }

    public String getLink() {
        return null;
    }

    @JsonIgnore
    public int getCacheDuration() {
        return Integer.MAX_VALUE;
    }
    @JsonIgnore
    public Optional<?> getLive() {
        return Optional.empty();
    }
    @JsonIgnore
    public final boolean isExpired() {
        return (System.nanoTime() - cachedAt) / 1000000000 > getCacheDuration();
    }
}
