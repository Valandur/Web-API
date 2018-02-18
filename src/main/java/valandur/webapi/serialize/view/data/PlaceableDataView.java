package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.manipulator.mutable.item.PlaceableData;
import valandur.webapi.api.cache.misc.CachedCatalogType;
import valandur.webapi.api.serialize.BaseView;

import java.util.Set;
import java.util.stream.Collectors;

public class PlaceableDataView extends BaseView<PlaceableData> {

    @JsonValue
    public Set<CachedCatalogType<BlockType>> types;


    public PlaceableDataView(PlaceableData value) {
        super(value);

        this.types = value.placeable().get().stream().map(CachedCatalogType::new).collect(Collectors.toSet());
    }
}
