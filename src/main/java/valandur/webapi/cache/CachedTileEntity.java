package valandur.webapi.cache;

import com.google.gson.annotations.Expose;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

public class CachedTileEntity extends CachedObject {
    @Expose
    public String type;

    @Expose
    public CachedLocation location;

    public Map<String, Object> data;
    public Map<String, Object> properties;
    public Collection<CachedItemStack> items;

    public static CachedTileEntity copyFrom(TileEntity te) {
        return copyFrom(te, false);
    }
    public static CachedTileEntity copyFrom(TileEntity te, boolean details) {
        CachedTileEntity cache = new CachedTileEntity();
        cache.type = te.getType().getId();
        cache.location = CachedLocation.copyFrom(te.getLocation());
        if (details) {
            cache.data = DataCache.containerToMap(te);
            cache.properties = DataCache.propertiesToMap(te);
            if (te instanceof TileEntityCarrier) {
                cache.items = new LinkedHashSet<>();
                Inventory inventory = ((TileEntityCarrier)te).getInventory();
                for (Inventory inv : inventory.slots()) {
                    Optional<ItemStack> stack = inv.peek();
                    if (stack.isPresent()) {
                        cache.items.add(CachedItemStack.copyFrom(stack.get()));
                    }
                }
            }
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return 0;
    }
}
