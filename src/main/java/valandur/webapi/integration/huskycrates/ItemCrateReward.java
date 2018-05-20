package valandur.webapi.integration.huskycrates;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.serialize.JsonDetails;

@ApiModel(value = "HuskyCratesItemReward", parent = CrateRewardObject.class)
public class ItemCrateReward extends CrateRewardObject {

    @Override
    public CrateRewardObjecType getType() {
        return CrateRewardObjecType.ITEM;
    }

    @JsonDeserialize
    private ItemStack item;
    @JsonDetails(value = false, simple = true)
    @ApiModelProperty(value = "The item that is awarded to the player", required = true)
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
