package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.item.BlockItemData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.block.CachedBlockState;

public class CachedBlockItemData extends CachedObject<BlockItemData> {

    @JsonValue
    public CachedBlockState state;


    public CachedBlockItemData(BlockItemData value) {
        super(value);

        this.state = new CachedBlockState(value.state().get());
    }
}
