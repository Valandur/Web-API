package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.spongepowered.api.world.DimensionType;

public class CachedDimension extends CachedObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;


    public static CachedDimension copyFrom(DimensionType dim) {
        CachedDimension cache = new CachedDimension();
        cache.id = dim.getId();
        cache.name = dim.getName();
        return cache;
    }
}
