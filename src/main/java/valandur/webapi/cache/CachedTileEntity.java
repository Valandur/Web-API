package valandur.webapi.cache;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import valandur.webapi.json.JsonConverter;

import java.lang.reflect.Field;
import java.util.*;

public class CachedTileEntity extends CachedObject {
    @JsonProperty
    public String type;

    @JsonProperty("class")
    public String clazz;

    @JsonProperty
    public CachedLocation location;

    public JsonNode data;
    public JsonNode properties;

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
            cache.properties = JsonConverter.toJson(te.getApplicableProperties(), true);
            try {
                cache.data = JsonConverter.toJson(te.toContainer(), true);
            } catch (Exception ex) {
                cache.data = JsonConverter.toJson(ex, true);
            }
            Field[] fs = JsonConverter.getAllFields(te.getClass());
        }
        return cache;
    }

    @Override
    public int getCacheDuration() {
        return CacheConfig.durationTileEntity;
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

    @Override
    @JsonProperty
    public String getLink() {
        return "/api/tile-entity/" + location.world.uuid + "/" + location.position.getFloorX() + "/" + location.position.getFloorY() + "/" + location.position.getFloorZ();
    }
}
