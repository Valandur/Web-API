package valandur.webapi.api.cache.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.ICachedObject;

import java.util.List;

@ApiModel("Inventory")
public interface ICachedInventory extends ICachedObject<Inventory> {

    @ApiModelProperty(value = "The name of the inventory", required = true)
    String getName();

    @ApiModelProperty(value = "The type of the inventory", required = true)
    CachedCatalogType getType();

    @ApiModelProperty(value = "The maximum capacity of the inventory (maximum number of stacks)", required = true)
    int getCapacity();

    @ApiModelProperty(value = "The total amount of items currently in the inventory", required = true)
    int getTotalItems();

    @ApiModelProperty(value = "Gets a list of item stacks in the inventory", required = true)
    List<ItemStack> getItemStacks();
}
