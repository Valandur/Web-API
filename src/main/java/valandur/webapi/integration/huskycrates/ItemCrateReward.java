package valandur.webapi.integration.huskycrates;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.serialize.JsonDetails;

public class ItemCrateReward extends CrateRewardObject {

    @Override
    public CrateRewardObjecType getType() {
        return CrateRewardObjecType.ITEM;
    }

    private ItemStack item;
    @JsonDetails(value = false, simple = true)
    public ItemStack getItem() {
        return item;
    }


    public ItemCrateReward() {}
    public ItemCrateReward(ItemStack item) {
        this.item = item;
    }

    @Override
    public void saveToNode(ConfigurationNode node) {
        super.saveToNode(node);
        node.getNode("overrideItem", "id").setValue(item.getItem().getId());
        node.getNode("overrideItem", "count").setValue(item.getQuantity());
    }
}
