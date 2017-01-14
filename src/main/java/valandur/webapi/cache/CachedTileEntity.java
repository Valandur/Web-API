package valandur.webapi.cache;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import valandur.webapi.misc.JsonConverter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

public class CachedTileEntity extends CachedObject {
    @Expose
    public String type;

    @Expose
    public CachedLocation location;

    public static CachedTileEntity copyFrom(TileEntity te) {
        return copyFrom(te, false);
    }
    public static CachedTileEntity copyFrom(TileEntity te, boolean details) {
        CachedTileEntity cache = new CachedTileEntity();
        cache.type = te.getType().getId();
        cache.location = CachedLocation.copyFrom(te.getLocation());
        if (details) {
            cache.details = true;
            cache.raw = JsonConverter.toJson(te);
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return 0;
    }
}
