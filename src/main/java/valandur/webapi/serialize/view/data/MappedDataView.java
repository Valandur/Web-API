package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.manipulator.mutable.MappedData;
import valandur.webapi.serialize.BaseView;

import java.util.HashMap;
import java.util.Map;

public class MappedDataView extends BaseView<MappedData> {

    @JsonValue
    public Map<String, Object> map = new HashMap<>();


    public MappedDataView(MappedData value) {
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
