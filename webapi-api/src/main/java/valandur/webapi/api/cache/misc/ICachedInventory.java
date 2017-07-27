package valandur.webapi.api.cache.misc;

import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.api.cache.ICachedObject;

import java.util.List;

public interface ICachedInventory extends ICachedObject {

    String getName();

    ICachedCatalogType getType();

    int getCapacity();

    int getTotalItems();

    List<ItemStack> getItems();
}
