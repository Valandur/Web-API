package valandur.webapi.serialize.view.item;

import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.serialize.BaseView;

import java.util.HashMap;
import java.util.Map;

public class ItemStackView extends BaseView<ItemStack> {

    public ItemType type;
    public int quantity;
    public Map<String, Object> data;


    public ItemStackView(ItemStack value) {
        super(value);

        this.type = value.getItem();
        this.quantity = value.getQuantity();
        this.data = new HashMap<>();
        for (Property<?, ?> property : value.getApplicableProperties()) {
            String key = property.getKey().toString();
            key = key.replace("Property", "");
            key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
            data.put(key, property.getValue());
        }
    }
}
