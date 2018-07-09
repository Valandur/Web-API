package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.ListData;
import valandur.webapi.cache.CachedObject;

import java.util.List;

public class CachedListData extends CachedObject<ListData> {

    @JsonValue
    public List<Object> list;


    public CachedListData(ListData value) {
        super(value);

        this.list = value.asList();
    }
}
