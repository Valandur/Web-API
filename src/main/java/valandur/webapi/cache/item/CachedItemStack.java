package valandur.webapi.cache.item;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.serialize.JsonDetails;

import javax.ws.rs.NotFoundException;
import java.util.Map;
import java.util.Optional;

@ApiModel("ItemStack")
public class CachedItemStack extends CachedObject<ItemStack> {

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
    @JsonAnyGetter
    @ApiModelProperty("Additional item data attached to this ItemStack")
    public Map<String, Object> getData() {
        return data;
    }


    public CachedItemStack(ItemStack value) {
        super(value);

        this.type = new CachedCatalogType<>(value.getType());
        this.quantity = value.getQuantity();

        // Add properties
        Map<Class<? extends Property<?, ?>>, String> props = WebAPI.getSerializeService().getSupportedProperties();
        for (Property<?, ?> property : value.getApplicableProperties()) {
            String key = props.get(property.getClass());
            if (key == null) {
                continue;
            }
            data.put(key, property.getValue());
        }

        // Add data
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
    }

    @Override
    public ItemStack getLive() {
        Optional<ItemType> optType = Sponge.getRegistry().getType(ItemType.class, this.getType().getId());
        if (!optType.isPresent())
            throw new NotFoundException("Could not find item type: " + this.getType().getId());

        ItemType type = optType.get();

        ItemStack.Builder builder = ItemStack.builder().itemType(type).quantity(this.quantity);
        return builder.build();
    }
}
