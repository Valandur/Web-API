package valandur.webapi.integration.nucleus;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonDeserialize
public class CreateKitRequest {

    @JsonDeserialize
    private String name;
    public String getName() {
        return name;
    }

    @JsonDeserialize
    private double cost;
    public double getCost() {
        return cost;
    }

    @JsonDeserialize
    private long interval;
    public Duration getInterval() {
        return Duration.ofSeconds(interval);
    }

    @JsonDeserialize
    private List<String> commands;
    public List<String> getCommands() {
        return commands != null ? commands : new ArrayList<>();
    }

    @JsonDeserialize
    private List<String> stacks;
    public List<ItemStackSnapshot> getStacks() {
        List<ItemStackSnapshot> snapshots = new ArrayList<>();
        if (stacks == null)
            return snapshots;

        for (String stack : stacks) {
            String[] splits = stack.split("\\|");
            if (splits.length == 0)
                continue;

            Optional<ItemType> type = Sponge.getRegistry().getType(ItemType.class, splits[0]);
            if (!type.isPresent())
                continue;

            int amount = 1;
            if (splits.length > 1) {
                amount = Integer.parseInt(splits[1]);
            }
            snapshots.add(ItemStack.of(type.get(), amount).createSnapshot());
        }
        return snapshots;
    }
}
