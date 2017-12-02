package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.item.InventoryItemData;
import org.spongepowered.api.item.inventory.Inventory;
import valandur.webapi.api.serialize.BaseView;

public class InventoryItemDataView extends BaseView<InventoryItemData> {

    @JsonValue
    public Inventory inventory;


    public InventoryItemDataView(InventoryItemData value) {
        super(value);

        this.inventory = value.getInventory();
    }
}
