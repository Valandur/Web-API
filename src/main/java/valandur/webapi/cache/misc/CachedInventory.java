package valandur.webapi.cache.misc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.item.CachedItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApiModel("Inventory")
public class CachedInventory extends CachedObject<Inventory> {

    private String name;
    @ApiModelProperty(value = "The name of the inventory", required = true)
    public String getName() {
        return name;
    }

    private CachedCatalogType<InventoryArchetype> type;
    @ApiModelProperty(value = "The type of the inventory", required = true)
    public CachedCatalogType<InventoryArchetype> getType() {
        return type;
    }

    private int capacity;
    @ApiModelProperty(value = "The maximum capacity of the inventory (maximum number of stacks)", required = true)
    public int getCapacity() {
        return capacity;
    }

    private int totalItems;
    @ApiModelProperty(value = "The total amount of items currently in the inventory", required = true)
    public int getTotalItems() {
        return totalItems;
    }

    private List<CachedItemStack> itemStacks;
    @ApiModelProperty(value = "Gets a list of item stacks in the inventory", required = true)
    public List<CachedItemStack> getItemStacks() {
        return itemStacks;
    }


    public CachedInventory(Inventory inv) {
        super(inv, false);

        this.name = inv.getName().get();
        this.capacity = inv.capacity();
        this.totalItems = inv.totalItems();
        this.type = new CachedCatalogType<>(inv.getArchetype());

        itemStacks = new ArrayList<>();
        try {
            for (Inventory subInv : inv.slots()) {
                Slot slot = (Slot) subInv;
                Optional<ItemStack> optItem = slot.peek();
                optItem.ifPresent(itemStack -> itemStacks.add(new CachedItemStack(itemStack)));
            }
        } catch (AbstractMethodError ignored) {}
    }
}
