package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.property.PropertyHolder;
import valandur.webapi.WebAPI;
import valandur.webapi.serialize.JsonDetails;
import valandur.webapi.serialize.SerializeService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The base class for all cached objects.
 */
@ApiModel("CachedObject")
public abstract class CachedObject<T> {
    protected long cachedAt;
    protected long cacheDuration = 0;

    protected CacheService cacheService;
    protected SerializeService serializeService;
    protected Class<? extends T> clazz;

    protected Map<String, Object> data = new HashMap<>();
    /**
     * Gets any data associated with the object if the original object was a {@link DataHolder}.
     * @return Data stored on the object.
     */
    @ApiModelProperty(hidden = true)
    @JsonDetails
    @JsonAnyGetter
    public Map<String, Object> getData() {
        return data;
    }


    public CachedObject(T value) {
        this(value, true);
    }
    public CachedObject(T value, boolean serializeData) {
        this.cachedAt = System.nanoTime();
        this.cacheService = WebAPI.getCacheService();
        this.serializeService = WebAPI.getSerializeService();

        this.cacheDuration = cacheService.getCacheDurationFor(this.getClass());

        if (value != null) this.clazz = (Class<? extends T>) value.getClass();

        if (serializeData) {
            if (value instanceof DataHolder) {
                DataHolder holder = (DataHolder)value;

                Map<String, Class<? extends DataManipulator<?, ?>>> supData = serializeService.getSupportedData();
                for (Map.Entry<String, Class<? extends DataManipulator<?, ?>>> entry : supData.entrySet()) {
                    try {
                        if (!holder.supports(entry.getValue()))
                            continue;

                        Optional<?> m = holder.get(entry.getValue());

                        if (!m.isPresent())
                            continue;

                        data.put(entry.getKey(), ((DataManipulator) m.get()).copy());
                    } catch (IllegalArgumentException | IllegalStateException ignored) {
                    }
                }
            }
            if (value instanceof PropertyHolder) {
                PropertyHolder holder = (PropertyHolder)value;

                Map<Class<? extends Property<?, ?>>, String> props = serializeService.getSupportedProperties();
                for (Property<?, ?> property : holder.getApplicableProperties()) {
                    String key = props.get(property.getClass());
                    if (data.containsKey(key)) {
                        key = key + "Property";
                    }
                    data.put(key, property.getValue());
                }
            }
        }
    }

    /**
     * Gets a relative link representing a Web-API endpoint where details for the object can be retrieved.
     * @return The link to the details of the object, or null if not applicable.
     */
    @ApiModelProperty(
            value = "The API link that can be used to obtain more information about this object",
            required = true,
            readOnly = true)
    public abstract String getLink();

    /**
     * Tries to get the live version this object is representing.
     * @return An optional containing the live version of this object if available, empty otherwise.
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    public Optional<T> getLive() {
        return Optional.empty();
    }

    /**
     * Checks if this cached object has expired.
     * @return True if this cached object is considered expired, false otherwise.
     */
    @ApiModelProperty(hidden = true)
    @JsonIgnore
    public final boolean isExpired() {
        return (System.nanoTime() - cachedAt) / 1000000000 > cacheDuration;
    }
}
