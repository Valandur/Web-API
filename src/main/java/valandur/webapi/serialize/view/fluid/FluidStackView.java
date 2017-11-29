package valandur.webapi.serialize.view.fluid;

import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.extra.fluid.FluidStack;
import org.spongepowered.api.extra.fluid.FluidType;
import valandur.webapi.WebAPI;
import valandur.webapi.api.serialize.BaseView;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FluidStackView extends BaseView<FluidStack> {

    public FluidType type;
    public int volume;


    public FluidStackView(FluidStack value) {
        super(value);

        this.type = value.getFluid();
        this.volume = value.getVolume();
    }

    @JsonDetails
    public Map<String, Object> getData() {
        HashMap<String, Object> data = new HashMap<>();
        Map<String, Class<? extends DataManipulator>> supData = WebAPI.getSerializeService().getSupportedData();
        for (Map.Entry<String, Class<? extends DataManipulator>> entry : supData.entrySet()) {
            Optional<?> m = value.get(entry.getValue());

            if (!m.isPresent())
                continue;

            data.put(entry.getKey(), ((DataManipulator)m.get()).copy());
        }
        return data;
    }
}
