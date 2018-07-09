package valandur.webapi.integration.nucleus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.nucleuspowered.nucleus.api.nucleusdata.Kit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.item.CachedItemStackSnapshot;
import valandur.webapi.serialize.JsonDetails;
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
    @ApiModelProperty(value = "True if this kit is awarded for joining the server the first time, false otherwise", required = true)
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
    @ApiModelProperty(value = "The commands that are executed when this kit is purchased/acquired by a player", required = true)
    public List<String> getCommands() {
        return commands;
    }

    private List<CachedItemStackSnapshot> stacks;
    @JsonDetails
    @ApiModelProperty(value = "The ItemStacks that are awarded to the player who buys/acquires this kit", required = true)
    public List<CachedItemStackSnapshot> getStacks() {
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
        this.stacks = kit.getStacks().stream().map(CachedItemStackSnapshot::new).collect(Collectors.toList());
    }

    @Override
    @JsonIgnore(false)
    public String getLink() {
        return Constants.BASE_PATH + "/nucleus/kit/" + name;
    }
}
