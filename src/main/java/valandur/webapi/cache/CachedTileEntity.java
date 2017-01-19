package valandur.webapi.cache;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.misc.JsonConverter;

import java.util.*;

public class CachedTileEntity extends CachedObject {
    @Expose
    public String type;

    @Expose
    @SerializedName("class")
    public String clazz;

    @Expose
    public CachedLocation location;

    public JsonElement data;
    public JsonElement properties;
    public JsonElement inventory;

    public static CachedTileEntity copyFrom(TileEntity te) {
        return copyFrom(te, false);
    }
    public static CachedTileEntity copyFrom(TileEntity te, boolean details) {
        CachedTileEntity cache = new CachedTileEntity();
        cache.type = te.getType() != null ? te.getType().getId() : null;
        cache.clazz = te.getClass().getName();
        cache.location = CachedLocation.copyFrom(te.getLocation());
        if (details) {
            cache.details = true;
            cache.data = JsonConverter.containerToJson(te);
            cache.properties = JsonConverter.propertiesToJson(te);
            if (te instanceof Inventory)
                cache.inventory = JsonConverter.inventoryToJson((Inventory)te);
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return 0;
    }
    @Override
    public Optional<Object> getLive() {
        Optional<Object> loc = location.getLive();
        if (!loc.isPresent())
            return Optional.empty();

        Optional<TileEntity> te = ((Location<World>)loc.get()).getTileEntity();

        if (!te.isPresent())
            return Optional.empty();
        return Optional.of(te.get());
    }
}
