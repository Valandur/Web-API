package valandur.webapi.cache.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.extra.fluid.data.manipulator.mutable.FluidItemData;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.fluid.CachedFluidStackSnapshot;

public class CachedFluidItemData extends CachedObject<FluidItemData> {

    @JsonValue
    public CachedFluidStackSnapshot fluid;


    public CachedFluidItemData(FluidItemData value) {
        super(value);

        this.fluid = new CachedFluidStackSnapshot(value.fluid().get());
    }
}
