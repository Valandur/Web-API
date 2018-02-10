package valandur.webapi.serialize.view.item;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.misc.CachedCatalogType;
import valandur.webapi.api.serialize.BaseView;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApiModel("ItemStack")
public class ItemStackView extends BaseView<ItemStack> {

    private CachedCatalogType<ItemType> type;
    @ApiModelProperty(value = "The type of this item", required = true)
    public CachedCatalogType<ItemType> getType() {
        return type;
    }

    private int quantity;
    @ApiModelProperty(value = "The quantity of items in this stack", required = true)
    public int getQuantity() {
        return quantity;
    }

    @JsonDetails
    @ApiModelProperty("Additional item data attached to this ItemStack")
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


    public ItemStackView(ItemStack value) {
        super(value);

        this.type = new CachedCatalogType<>(value.getType());
        this.quantity = value.getQuantity();
    }
}
