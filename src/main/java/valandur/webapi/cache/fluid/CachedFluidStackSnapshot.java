package valandur.webapi.cache.fluid;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.extra.fluid.FluidStackSnapshot;
import valandur.webapi.cache.CachedObject;

public class CachedFluidStackSnapshot extends CachedObject<FluidStackSnapshot> {

    @JsonValue
    public CachedFluidStack fluid;


    public CachedFluidStackSnapshot(FluidStackSnapshot value) {
        super(value);

        this.fluid = new CachedFluidStack(value.createStack());
    }
}
