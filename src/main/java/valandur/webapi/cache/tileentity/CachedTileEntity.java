package valandur.webapi.cache.tileentity;

import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CacheConfig;
import valandur.webapi.cache.misc.CachedLocation;
import valandur.webapi.cache.CachedObject;

import java.util.*;

public class CachedTileEntity extends CachedObject {

    private String type;
    public String getType() {
        return type;
    }

    private CachedLocation location;
    public CachedLocation getLocation() {
        return location;
    }

    protected Inventory inventory;
    public Inventory getInventory() {
        return inventory;
    }


    public CachedTileEntity(TileEntity te) {
        super(te);

        this.type = te.getType().getId();
        this.location = new CachedLocation(te.getLocation());

        if (te instanceof TileEntityCarrier) {
            Inventory inv = ((TileEntityCarrier)te).getInventory();
            this.inventory = Inventory.builder().from(inv).build(WebAPI.getInstance());
        }
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.durationTileEntity;
    }
    @Override
    public Optional<?> getLive() {
        Optional<?> obj = location.getLive();
        return obj.flatMap(o -> ((Location<World>) o).getTileEntity());
    }

    @Override
    public String getLink() {
        return "/api/tile-entity/" + location.getWorld().getUUID() + "/" + location.getPosition().getFloorX() + "/" +
                location.getPosition().getFloorY() + "/" + location.getPosition().getFloorZ();
    }
}
