package valandur.webapi.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.serialize.JsonDetails;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CachedKit extends CachedObject<Kit> {

    private String name;
    public String getName() {
        return name;
    }

    private Double cost;
    public Double getCost() {
        return cost;
    }

    private Long cooldown;
    public Long getCooldown() {
        return cooldown;
    }

    private Boolean firstJoinKit;
    public Boolean isFirstJoinKit() {
        return firstJoinKit;
    }

    private Boolean oneTime;
    public Boolean isOneTime() {
        return oneTime;
    }

    private List<String> commands;
    @JsonDetails
    public List<String> getCommands() {
        return commands;
    }

    private List<ItemStackSnapshot> stacks;
    @JsonDetails
    public List<ItemStackSnapshot> getStacks() {
        return stacks;
    }


    public CachedKit() {
        super(null);
    }
    public CachedKit(Kit kit) {
        super(kit);

        this.name = kit.getName();
        this.cost = kit.getCost();
        this.cooldown = kit.getCooldown().orElse(Duration.ofNanos(0)).toMillis();
        this.firstJoinKit = kit.isFirstJoinKit();
        this.oneTime = kit.isOneTime();
        this.commands = new ArrayList<>(kit.getCommands());
        this.stacks = kit.getStacks().stream().map(ValueContainer::copy).collect(Collectors.toList());
    }
}
