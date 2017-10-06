package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.crate.config.CrateReward;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.json.JsonDetails;

import java.util.List;
import java.util.stream.Collectors;

public class CachedCrateReward extends CachedObject<CrateReward> {

    private String name;
    public String getName() {
        return name;
    }

    private double chance;
    public double getChance() {
        return chance;
    }

    private ItemStack displayItem;
    @JsonDetails
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    private boolean shouldAnnounce;
    @JsonDetails
    public boolean shouldAnnounce() {
        return shouldAnnounce;
    }

    private List<Object> rewards;
    @JsonDetails
    public List<Object> getRewards() {
        return rewards;
    }


    public CachedCrateReward(CrateReward reward) {
        super(reward);

        this.name = reward.getRewardName();
        this.chance = reward.getChance();
        this.displayItem = reward.getDisplayItem().copy();
        this.shouldAnnounce = reward.shouldAnnounce();
        this.rewards = reward.getRewards().stream()
                .map(o -> o instanceof ItemStack ? ((ItemStack)o).copy() : o)
                .collect(Collectors.toList());
    }
}
