package valandur.webapi.serialize.view.item;

import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.WebAPI;
import valandur.webapi.api.serialize.BaseView;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemStackView extends BaseView<ItemStack> {

    public ItemType type;
    public int quantity;


    public ItemStackView(ItemStack value) {
        super(value);

        this.type = value.getItem();
        this.quantity = value.getQuantity();
    }

    @JsonDetails
    public Map<String, Object> getData() {
        HashMap<String, Object> data = new HashMap<>();
        // Add properties
        for (Property<?, ?> property : value.getApplicableProperties()) {
            String key = property.getKey().toString();
            key = key.replace("Property", "");
            key = Character.toLowerCase(key.charAt(0)) + key.substring(1);
            data.put(key, property.getValue());
        }
        // Add data
        Map<String, Class<? extends DataManipulator<?, ?>>> supData = WebAPI.getSerializeService().getSupportedData();
        for (Map.Entry<String, Class<? extends DataManipulator<?, ?>>> entry : supData.entrySet()) {
            try {
                if (!value.supports(entry.getValue()))
                    continue;

                Optional<?> m = value.get(entry.getValue());

                if (!m.isPresent())
                    continue;

                data.put(entry.getKey(), ((DataManipulator) m.get()).copy());
            } catch (IllegalArgumentException | IllegalStateException ignored) {
            }
        }
        return data;
    }
}
