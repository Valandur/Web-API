package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.item.InventoryItemData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedInventory;

public class CachedInventoryItemData extends CachedObject<InventoryItemData> {

    @JsonValue
    public CachedInventory inventory;


    public CachedInventoryItemData(InventoryItemData value) {
        super(value);

        this.inventory = new CachedInventory(value.getInventory());
    }
}
