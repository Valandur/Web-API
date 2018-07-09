package valandur.webapi.cache.tileentity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.cache.misc.CachedInventory;
import valandur.webapi.cache.world.CachedLocation;
import valandur.webapi.serialize.JsonDetails;
import valandur.webapi.util.Constants;

import javax.ws.rs.NotFoundException;
import java.util.Optional;

@ApiModel("TileEntity")
public class CachedTileEntity extends CachedObject<TileEntity> {

    private CachedCatalogType type;
    @ApiModelProperty(value = "The type of this tile entity", required = true)
    public CachedCatalogType getType() {
        return type;
    }

    private CachedLocation location;
    @ApiModelProperty(value = "The location of this tile entity", required = true)
    public CachedLocation getLocation() {
        return location;
    }

    protected CachedInventory inventory;
    @JsonDetails
    @ApiModelProperty("The inventory this tile entity has (if any)")
    public CachedInventory getInventory() {
        return inventory;
    }


    public CachedTileEntity(TileEntity te) {
        super(te);

        this.type = te.getType() instanceof CatalogType ? new CachedCatalogType(te.getType()) : null;
        this.location = new CachedLocation(te.getLocation());

        if (te instanceof TileEntityCarrier) {
            this.inventory = new CachedInventory(((TileEntityCarrier)te).getInventory());
        }
    }

    @Override
    public TileEntity getLive() {
        Optional<TileEntity> optEnt = location.getLive().getTileEntity();
        if (!optEnt.isPresent()) {
            throw new NotFoundException("Could not find tile entity");
        }
        return optEnt.get();
    }

    @Override
    @JsonIgnore(false)
    public String getLink() {
        return Constants.BASE_PATH + "/tile-entity/" +
                location.getWorld().getUUID() + "/" +
                location.getPosition().x + "/" +
                location.getPosition().y + "/" +
                location.getPosition().z;
    }
}
