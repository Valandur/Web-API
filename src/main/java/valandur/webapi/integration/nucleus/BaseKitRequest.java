package valandur.webapi.integration.nucleus;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.item.inventory.ItemStack;

import java.time.Duration;
import java.util.List;

@JsonDeserialize
public class BaseKitRequest {

    @JsonDeserialize
    private Double cost;
    public double getCost() {
        return cost;
    }
    public boolean hasCost() {
        return cost != null;
    }

    @JsonDeserialize
    private Long cooldown;
    public Duration getCooldown() {
        return Duration.ofMillis(cooldown);
    }
    public boolean hasCooldown() {
        return cooldown != null;
    }

    @JsonDeserialize
    private List<String> commands;
    public List<String> getCommands() {
        return commands;
    }
    public boolean hasCommands() {
        return commands != null;
    }

    @JsonDeserialize
    private List<ItemStack> stacks;
    public List<ItemStack> getStacks() throws Exception {
        return stacks;
    }
    public boolean hasStacks() {
        return stacks != null;
    }
}
