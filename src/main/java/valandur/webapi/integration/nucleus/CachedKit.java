package valandur.webapi.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CachedKit {

    private String name;
    public String getName() {
        return name;
    }

    private double cost;
    public double getCost() {
        return cost;
    }

    private Duration interval;
    public Duration getInterval() {
        return interval;
    }

    private boolean isFirstJoinKit;
    public boolean isFirstJoinKit() {
        return isFirstJoinKit;
    }

    private boolean isOneTime;
    public boolean isOneTime() {
        return isOneTime;
    }

    private List<String> commands;
    public List<String> getCommands() {
        return commands;
    }

    private List<ItemStackSnapshot> stacks;
    public List<ItemStackSnapshot> getStacks() {
        return stacks;
    }


    public CachedKit(String name, Kit kit) {
        this.name = name;
        this.cost = kit.getCost();
        this.interval = kit.getInterval();
        this.isFirstJoinKit = kit.isFirstJoinKit();
        this.isOneTime = kit.isOneTime();
        this.commands = new ArrayList<>(kit.getCommands());
        this.stacks = kit.getStacks().stream().map(ValueContainer::copy).collect(Collectors.toList());
    }
}
