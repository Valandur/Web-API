package valandur.webapi.integration.huskycrates;

import com.codehusky.huskycrates.crate.config.CrateReward;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class CachedCrateReward {

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

    private boolean shouldAnnounce;
    public boolean shouldAnnounce() {
        return shouldAnnounce;
    }

    private List<Object> rewards;
    public List<Object> getRewards() {
        return rewards;
    }


    public CachedCrateReward(CrateReward reward) {
        this.name = reward.getRewardName();
        this.chance = reward.getChance();
        this.displayItem = reward.getDisplayItem().copy();
        this.shouldAnnounce = reward.shouldAnnounce();
        this.rewards = reward.getRewards().stream()
                .map(o -> o instanceof ItemStack ? ((ItemStack)o).copy() : o)
                .collect(Collectors.toList());
    }
}
