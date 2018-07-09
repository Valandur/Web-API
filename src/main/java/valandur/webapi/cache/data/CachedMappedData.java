package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.manipulator.mutable.MappedData;
import valandur.webapi.cache.CachedObject;

import java.util.HashMap;
import java.util.Map;

public class CachedMappedData extends CachedObject<MappedData> {

    @JsonValue
    public Map<String, Object> map = new HashMap<>();


    public CachedMappedData(MappedData value) {
        super(value);

        Map<Object, Object> data = value.asMap();
        for (Map.Entry entry : data.entrySet()) {
            String key = entry.getKey().toString();
            if (entry.getKey() instanceof CatalogType) {
                key = ((CatalogType)entry.getKey()).getId();
            }
            map.put(key, entry.getValue());
        }
    }
}
