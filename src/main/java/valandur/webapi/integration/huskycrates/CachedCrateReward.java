package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.crate.config.CrateReward;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.List;
import java.util.stream.Collectors;

@ConfigSerializable
public class CachedCrateReward extends CachedObject<CrateReward> {

    @JsonDeserialize
    @Setting
    private String name;
    public String getName() {
        return name;
    }

    @JsonDeserialize
    @Setting
    private double chance;
    public double getChance() {
        return chance;
    }

    @Setting
    @JsonDetails(value = false, simple = true)
    private ItemStack displayItem;
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    @JsonDeserialize
    @Setting
    private boolean announce;
    public boolean isAnnounce() {
        return announce;
    }

    @JsonIgnore
    private List<CrateRewardObject> objects;
    @JsonGetter
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
}
