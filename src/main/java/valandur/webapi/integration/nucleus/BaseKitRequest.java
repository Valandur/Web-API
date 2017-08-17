package valandur.webapi.integration.nucleus;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStack.Builder;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@JsonDeserialize
public class BaseKitRequest {

    @JsonDeserialize
    private Double cost;
    public double getCost() {
        return cost != null ? cost : 0;
    }
    public boolean hasCost() {
        return cost != null;
    }

    @JsonDeserialize
    private Long interval;
    public Duration getInterval() {
        return Duration.ofSeconds(interval != null ? interval : 0);
    }
    public boolean hasInterval() {
        return interval != null;
    }

    @JsonDeserialize
    private List<String> commands;
    public List<String> getCommands() {
        return commands != null ? commands : new ArrayList<>();
    }
    public boolean hasCommands() {
        return commands != null;
    }

    @JsonDeserialize
    private List<ItemStackRequest> stacks;
    public List<ItemStackSnapshot> getStacks() throws Exception {
        List<ItemStackSnapshot> res = new ArrayList<>();
        for (BaseKitRequest.ItemStackRequest stack : stacks) {
            res.add(stack.getStackSnapshot());
        }
        return res;
    }
    public boolean hasStacks() {
        return stacks != null;
    }


    public static class ItemStackRequest {

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

            Builder builder = ItemStack.builder().itemType(type).quantity(getQuantity());
            return builder.build().createSnapshot();
        }
    }
}
