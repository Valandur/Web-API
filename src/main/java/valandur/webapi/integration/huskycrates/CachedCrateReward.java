/*package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.crate.config.CrateReward;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.serialize.JsonDetails;

import java.util.List;
import java.util.stream.Collectors;

@ConfigSerializable
@ApiModel("HuskyCratesCrateReward")
public class CachedCrateReward extends CachedObject<CrateReward> {

    @Setting
    private String name;
    @ApiModelProperty(value = "The name of this reward", required = true)
    public String getName() {
        return name;
    }

    @Setting
    private double chance;
    @ApiModelProperty(value = "The chance to aquire this reward. This is relative to the chances of the other rewards in this crate", required = true)
    public double getChance() {
        return chance;
    }

    @Setting
    @JsonDetails(value = false, simple = true)
    private ItemStack displayItem;
    @ApiModelProperty(value = "The ItemStack that is shown in the UI", required = true)
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    @Setting
    private boolean announce;
    @ApiModelProperty(value = "True if this reward is announced in chat, false otherwise", required = true)
    public boolean isAnnounce() {
        return announce;
    }

    @JsonIgnore
    private List<CrateRewardObject> objects;
    @JsonGetter
    @ApiModelProperty(value = "The objects that are rewarded as part of this reward (can be commands and/or items)", required = true)
    public List<CrateRewardObject> getObjects() {
        return objects;
    }
    @JsonSetter
    public void setObjects(List<CrateRewardObject> objects) {
        this.objects = objects;
    }


    public CachedCrateReward() {
        super(null);
    }
    public CachedCrateReward(CrateReward reward) {
        super(reward);

        this.name = reward.getRewardName();
        this.chance = reward.getChance();
        this.displayItem = reward.getDisplayItem().copy();
        this.announce = reward.shouldAnnounce();
        this.objects = reward.getRewards().stream().map(this::getRewardObject).collect(Collectors.toList());
    }

    private CrateRewardObject getRewardObject(Object obj) {
        if (obj instanceof ItemStack)
            return new ItemCrateReward(((ItemStack)obj).copy());
        if (obj instanceof String)
            return new CommandCrateReward(obj.toString());
        return null;
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
*/