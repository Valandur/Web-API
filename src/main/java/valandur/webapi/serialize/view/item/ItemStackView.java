package valandur.webapi.serialize.view.item;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.WebAPI;
import valandur.webapi.api.serialize.BaseView;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApiModel("ItemStack")
public class ItemStackView extends BaseView<ItemStack> {

    @ApiModelProperty(value = "The type of this item", required = true)
    public ItemType getType() {
        return value.getItem();
    }

    @ApiModelProperty(value = "The quantity of items in this stack", required = true)
    public int getQuantity() {
        return value.getQuantity();
    }

    @JsonDetails
    @JsonAnyGetter
    @ApiModelProperty("Additional item data attached to this ItemStack")
    public Map<String, Object> getData() {
        HashMap<String, Object> data = new HashMap<>();

        // Add properties
        Map<Class<? extends Property<?, ?>>, String> props = WebAPI.getSerializeService().getSupportedProperties();
        for (Property<?, ?> property : value.getApplicableProperties()) {
            String key = props.get(property.getClass());
            data.put(key, property.getValue());
        }

        // Add data
        for (ImmutableValue<?> immutableValue : value.getValues()) {
            WebAPI.getLogger().info(immutableValue.getKey().toString());
        }
        for (Map.Entry<String, Class<? extends DataManipulator<?, ?>>> entry :
                WebAPI.getSerializeService().getSupportedData().entrySet()) {
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
    }
}
