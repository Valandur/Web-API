package valandur.webapi.cache;

import com.google.gson.annotations.Expose;
import org.spongepowered.api.item.inventory.ItemStack;

public class CachedItemStack extends CachedObject {
    @Expose
    public String id;

    @Expose
    public String name;

    @Expose
    public int quantity;

    public static CachedItemStack copyFrom(ItemStack stack) {
        CachedItemStack cache = new CachedItemStack();
        cache.id = stack.getItem().getId();
        cache.name = stack.getTranslation().get();
        cache.quantity = stack.getQuantity();
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return 0;
    }
}
