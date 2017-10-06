package valandur.webapi.json.view.item;

import org.spongepowered.api.item.ItemType;
import valandur.webapi.api.json.BaseView;

public class ItemTypeView extends BaseView<ItemType> {

    public String id;
    public String name;


    public ItemTypeView(ItemType value) {
        super(value);

        this.id = value.getId();
        this.name = value.getTranslation().get();
    }
}
