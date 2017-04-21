package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import valandur.webapi.json.JsonConverter;

import java.util.Map;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class CachedObject {

    private transient long cachedAt;

    @JsonProperty("class")
    public JsonNode clazz;

    @JsonAnyGetter
    protected Map<String, JsonNode> getData() {
        return data;
    }
    protected Map<String, JsonNode> data;

    public CachedObject(Object obj) {
        this.cachedAt = System.nanoTime();
        if (obj != null) this.clazz = JsonConverter.toJson(obj.getClass().getName());
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
