package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.manipulator.mutable.item.PlaceableData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;

import java.util.Set;
import java.util.stream.Collectors;

public class CachedPlaceableData extends CachedObject<PlaceableData> {

    @JsonValue
    public Set<CachedCatalogType<BlockType>> types;


    public CachedPlaceableData(PlaceableData value) {
        super(value);

        this.types = value.placeable().get().stream().map(CachedCatalogType::new).collect(Collectors.toSet());
    }
}
