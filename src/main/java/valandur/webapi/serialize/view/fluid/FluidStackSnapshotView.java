package valandur.webapi.serialize.view.fluid;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.extra.fluid.FluidStack;
import org.spongepowered.api.extra.fluid.FluidStackSnapshot;
import valandur.webapi.serialize.BaseView;

public class FluidStackSnapshotView extends BaseView<FluidStackSnapshot> {

    @JsonValue
    public FluidStack fluid;


    public FluidStackSnapshotView(FluidStackSnapshot value) {
        super(value);

        this.fluid = value.createStack();
    }
}
