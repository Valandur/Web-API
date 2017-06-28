package valandur.webapi.api.cache.misc;

import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import valandur.webapi.api.cache.CachedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CachedInventory extends CachedObject {

    private String name;
    public String getName() {
        return name;
    }

    private CachedCatalogType type;
    public CachedCatalogType getType() {
        return type;
    }

    private int capacity;
    public int getCapacity() {
        return capacity;
    }

    private int totalItems;
    public int getTotalItems() {
        return totalItems;
    }

    private List<ItemStack> items;
    public List<ItemStack> getItems() {
        return items;
    }


    public CachedInventory(Inventory inv) {
        super(inv);

        this.name = inv.getName().get();
        this.capacity = inv.capacity();
        this.totalItems = inv.totalItems();
        this.type = new CachedCatalogType(inv.getArchetype());

        items = new ArrayList<>();
        try {
            for (Inventory subInv : inv.slots()) {
                Slot slot = (Slot) subInv;
                Optional<ItemStack> optItem = slot.peek();
                optItem.ifPresent(itemStack -> items.add(itemStack.copy()));
            }
        } catch (AbstractMethodError ignored) {
        }
    }
}
