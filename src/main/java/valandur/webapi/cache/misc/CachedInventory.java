package valandur.webapi.cache.misc;

import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import valandur.webapi.api.cache.CachedObject;
import valandur.webapi.api.cache.misc.CachedCatalogType;
import valandur.webapi.api.cache.misc.ICachedInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CachedInventory extends CachedObject<Inventory> implements ICachedInventory {

    private String name;
    @Override
    public String getName() {
        return name;
    }

    private CachedCatalogType type;
    @Override
    public CachedCatalogType getType() {
        return type;
    }

    private int capacity;
    @Override
    public int getCapacity() {
        return capacity;
    }

    private int totalItems;
    @Override
    public int getTotalItems() {
        return totalItems;
    }

    private List<ItemStack> itemStacks;
    @Override
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
}
