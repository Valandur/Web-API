package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.FluidLevelData;
import valandur.webapi.cache.CachedObject;

public class CachedFluidLevelData extends CachedObject<FluidLevelData> {

    @JsonValue
    public int level;


    public CachedFluidLevelData(FluidLevelData value) {
        super(value);

        this.level = value.level().get();
    }
}
