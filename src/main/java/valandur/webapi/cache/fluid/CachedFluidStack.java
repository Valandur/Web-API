package valandur.webapi.cache.fluid;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.extra.fluid.FluidStack;
import org.spongepowered.api.extra.fluid.FluidType;
import valandur.webapi.WebAPI;
import valandur.webapi.cache.CachedObject;
import valandur.webapi.cache.misc.CachedCatalogType;
import valandur.webapi.serialize.JsonDetails;

import java.util.Map;
import java.util.Optional;

@ApiModel("FluidStack")
public class CachedFluidStack extends CachedObject<FluidStack> {

    private CachedCatalogType<FluidType> type;
    @ApiModelProperty(value = "The type of fluid contained within this stack", required = true)
    public CachedCatalogType<FluidType> getType() {
        return type;
    }

    private int volume;
    @ApiModelProperty(value = "The amount of fluid in this stack", required = true)
    public int getVolume() {
        return volume;
    }

    @JsonDetails
    @ApiModelProperty("Additional item data attached to this FluidStack")
    public Map<String, Object> getData() {
        return this.data;
    }


    public CachedFluidStack(FluidStack value) {
        super(value);

        this.type = new CachedCatalogType<>(value.getFluid());
        this.volume = value.getVolume();

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
    }
}
