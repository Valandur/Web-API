package valandur.webapi.integration.nucleus;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.json.request.item.ItemStackRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

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
        for (ItemStackRequest stack : stacks) {
            res.add(stack.getStackSnapshot());
        }
        return res;
    }
    public boolean hasStacks() {
        return stacks != null;
    }
}
