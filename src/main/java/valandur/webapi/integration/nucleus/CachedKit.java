package valandur.webapi.integration.nucleus;

import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApiModel("NucleusKit")
public class CachedKit extends CachedObject<Kit> {

    private String name;
    @ApiModelProperty(value = "The unique name of this kit", required = true)
    public String getName() {
        return name;
    }

    private Double cost;
    @ApiModelProperty(value = "The cost to buy this kit", required = true)
    public Double getCost() {
        return cost;
    }

    private Long cooldown;
    @ApiModelProperty(value = "The cooldown (in seconds) this kit is on after buying it (per player)", required = true)
    public Long getCooldown() {
        return cooldown;
    }

    private Boolean firstJoinKit;
    @ApiModelProperty(
            value = "True if this kit is awarded for joining the server the first time, false otherwise",
            required = true)
    public Boolean isFirstJoinKit() {
        return firstJoinKit;
    }

    private Boolean oneTime;
    @ApiModelProperty(value = "True if this kit can only be purchased/acquired once, false otherwise", required = true)
    public Boolean isOneTime() {
        return oneTime;
    }

    private List<String> commands;
    @JsonDetails
    @ApiModelProperty("The commands that are executed when this kit is purchased/acquired by a player")
    public List<String> getCommands() {
        return commands;
    }

    private List<ItemStackSnapshot> stacks;
    @JsonDetails
    @ApiModelProperty("The ItemStacks that are awarded to the player who buys/acquires this kit")
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

    @Override
    public String getLink() {
        return Constants.BASE_PATH + "/kit/" + name;
    }
}
