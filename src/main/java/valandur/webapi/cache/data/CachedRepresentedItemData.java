package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.RepresentedItemData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.item.CachedItemStackSnapshot;

public class CachedRepresentedItemData extends CachedObject<RepresentedItemData> {

    @JsonValue
    public CachedItemStackSnapshot item;


    public CachedRepresentedItemData(RepresentedItemData value) {
        super(value);

        this.item = new CachedItemStackSnapshot(value.item().get());
    }
}
