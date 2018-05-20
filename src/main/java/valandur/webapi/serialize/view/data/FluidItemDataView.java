package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.extra.fluid.FluidStackSnapshot;
import org.spongepowered.api.extra.fluid.data.manipulator.mutable.FluidItemData;
import valandur.webapi.serialize.BaseView;

public class FluidItemDataView extends BaseView<FluidItemData> {

    @JsonValue
    public FluidStackSnapshot fluid;


    public FluidItemDataView(FluidItemData value) {
        super(value);

        this.fluid = value.fluid().get();
    }
}
