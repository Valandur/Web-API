package valandur.webapi.api.serialize.request.item;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.Map;
import java.util.Optional;

public class ItemStackRequest {

    @JsonDeserialize
    private JsonNode type;
    public Optional<ItemType> getType() {
        String typeId = type.get("id").asText();
        return Sponge.getRegistry().getType(ItemType.class, typeId);
    }

    @JsonDeserialize
    private Integer quantity;
    public Integer getQuantity() {
        return quantity != null ? quantity : 1;
    }

    @JsonDeserialize
    private Map<String, JsonNode> data;
    public Map<String, JsonNode> getData() {
        return data;
    }


    public ItemStackSnapshot getStackSnapshot() throws Exception {
        Optional<ItemType> optType = getType();
        if (!optType.isPresent())
            throw new Exception("Invalid item type");

        ItemType type = optType.get();

        ItemStack.Builder builder = ItemStack.builder().itemType(type).quantity(getQuantity());
        return builder.build().createSnapshot();
    }
}
