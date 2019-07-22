package valandur.webapi.serialize.view.fluid;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.extra.fluid.FluidStack;
import org.spongepowered.api.extra.fluid.FluidType;
import valandur.webapi.WebAPI;
import valandur.webapi.serialize.BaseView;
import valandur.webapi.serialize.JsonDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApiModel("FluidStack")
public class FluidStackView extends BaseView<FluidStack> {

    @ApiModelProperty(value = "The type of fluid contained within this stack", required = true)
    public FluidType getType() {
        return value.getFluid();
    }

    @ApiModelProperty(value = "The amount of fluid in this stack", required = true)
    public int getVolume() {
        return value.getVolume();
    }

    @JsonDetails
    @JsonAnyGetter
    @ApiModelProperty("Additional item data attached to this FluidStack")
    public Map<String, Object> getData() {
        HashMap<String, Object> data = new HashMap<>();

        // Add properties
        Map<Class<? extends Property<?, ?>>, String> props = WebAPI.getSerializeService().getSupportedProperties();
        for (Property<?, ?> property : value.getApplicableProperties()) {
            String key = props.get(property.getClass());
            if (key == null) {
                continue;
            }
            data.put(key, property.getValue());
        }

        // Add data
        Map<String, Class<? extends DataManipulator<?, ?>>> supData = WebAPI.getSerializeService().getSupportedData();
        for (Map.Entry<String, Class<? extends DataManipulator<?, ?>>> entry : supData.entrySet()) {
            try {
                if (!value.supports(entry.getValue()))
                    continue;

                Optional<?> m = value.get(entry.getValue());

                if (!m.isPresent())
                    continue;

                data.put(entry.getKey(), ((DataManipulator) m.get()).copy());
            } catch (IllegalArgumentException | IllegalStateException ignored) {
            }
        }

        return data;
    }


    public FluidStackView(FluidStack value) {
        super(value);
    }
}
