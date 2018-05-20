package valandur.webapi.cache.misc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import valandur.webapi.cache.CachedObject;

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

    private CachedCatalogType type;
    @ApiModelProperty(value = "The type of the inventory", required = true)
    public CachedCatalogType getType() {
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

    private List<ItemStack> itemStacks;
    @ApiModelProperty(value = "Gets a list of item stacks in the inventory", required = true)
    public List<ItemStack> getItemStacks() {
        return itemStacks;
    }


    public CachedInventory(Inventory inv) {
        super(inv, false);

        this.name = inv.getName().get();
        this.capacity = inv.capacity();
        this.totalItems = inv.totalItems();
        this.type = new CachedCatalogType(inv.getArchetype());

        itemStacks = new ArrayList<>();
        try {
            for (Inventory subInv : inv.slots()) {
                Slot slot = (Slot) subInv;
                Optional<ItemStack> optItem = slot.peek();
                optItem.ifPresent(itemStack -> itemStacks.add(itemStack.copy()));
            }
        } catch (AbstractMethodError ignored) {}
    }

    @Override
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    public String getLink() {
        return null;
    }
}
