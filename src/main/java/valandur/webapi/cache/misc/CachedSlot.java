package valandur.webapi.cache.misc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedObject;

import java.util.Map;

@ApiModel("Slot")
public class CachedSlot extends CachedObject<Slot> {

    private ItemStack stack;
    @ApiModelProperty(value = "The item stack that is in this slot", required = true)
    public ItemStack getStack() {
        return stack;
    }


    public CachedSlot(Slot value) {
        super(value);

        this.stack = value.peek().copy();

        // Add properties
        Map<Class<? extends Property<?, ?>>, String> props = WebAPI.getSerializeService().getSupportedProperties();
        for (Map.Entry<Class<? extends Property<?, ?>>, String> entry : props.entrySet()) {
            if (InventoryProperty.class.isAssignableFrom(entry.getKey())) {
                Class<? extends InventoryProperty<?, ?>> propClass =
                        (Class<? extends InventoryProperty<?, ?>>) entry.getKey();
                value.getProperty(propClass).ifPresent(val -> data.put(entry.getValue(), val.getValue()));
            }
        }
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
