package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.RepresentedItemData;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.serialize.BaseView;

public class RepresentedItemDataView extends BaseView<RepresentedItemData> {

    @JsonValue
    public ItemStackSnapshot item;


    public RepresentedItemDataView(RepresentedItemData value) {
        super(value);

        this.item = value.item().get();
    }
}
