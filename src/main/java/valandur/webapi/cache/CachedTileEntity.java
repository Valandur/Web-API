package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.json.JsonConverter;

import java.util.*;

public class CachedTileEntity extends CachedObject {
    @JsonProperty
    public String type;

    @JsonProperty("class")
    public String clazz;

    @JsonIgnore
    private CachedLocation _location;

    @JsonProperty
    public JsonNode location;

    public JsonNode inventory;


    public CachedTileEntity(TileEntity te) {
        this.type = te.getType() != null ? te.getType().getId() : null;
        this.clazz = te.getClass().getName();
        this._location = new CachedLocation(te.getLocation());
        this.location = JsonConverter.toJson(this._location);
        //if (te instanceof TileEntityCarrier)
        //    this.inventory = JsonConverter.toJson(((TileEntityCarrier)te).getInventory());
        this.data = JsonConverter.dataHolderToJson(te);
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.durationTileEntity;
    }
    @Override
    public Optional<?> getLive() {
        Optional<?> obj = _location.getLive();
        return obj.flatMap(o -> ((Location<World>) o).getTileEntity());
    }

    @Override
    @JsonProperty
    public String getLink() {
        return "/api/tile-entity/" + _location.getWorldUUID() + "/" + _location.position.getFloorX() + "/" + _location.position.getFloorY() + "/" + _location.position.getFloorZ();
    }
}
