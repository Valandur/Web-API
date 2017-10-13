package valandur.webapi.serialize.view.item;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.api.serialize.BaseView;

public class ItemStackSnapshotView extends BaseView<ItemStackSnapshot> {

    @JsonValue
    public ItemStack stack;


    public ItemStackSnapshotView(ItemStackSnapshot value) {
        super(value);

        this.stack = value.createStack();
    }
}
