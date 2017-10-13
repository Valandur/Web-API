package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.crate.config.CrateReward;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.CachedObject;

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
    public ItemStack getDisplayItem() {
        return displayItem;
    }

    private boolean announce;
    public boolean isAnnounce() {
        return announce;
    }

    private List<Object> rewards;
    public List<Object> getRewards() {
        return rewards;
    }


    public CachedCrateReward(CrateReward reward) {
        super(reward);

        this.name = reward.getRewardName();
        this.chance = reward.getChance();
        this.displayItem = reward.getDisplayItem().copy();
        this.announce = reward.shouldAnnounce();
        this.rewards = reward.getRewards().stream()
                .map(o -> o instanceof ItemStack ? ((ItemStack)o).copy() : o)
                .collect(Collectors.toList());
    }
}
