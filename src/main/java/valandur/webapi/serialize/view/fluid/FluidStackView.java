package valandur.webapi.serialize.view.fluid;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.extra.fluid.FluidStack;
import org.spongepowered.api.extra.fluid.FluidType;
import valandur.webapi.WebAPI;
import valandur.webapi.api.cache.misc.CachedCatalogType;
import valandur.webapi.api.serialize.BaseView;
import valandur.webapi.api.serialize.JsonDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApiModel("FluidStack")
public class FluidStackView extends BaseView<FluidStack> {

    @ApiModelProperty(value = "The type of fluid contained within this stack", required = true)
    public CachedCatalogType<FluidType> type;

    @ApiModelProperty(value = "The amount of fluid in this stack", required = true)
    public int volume;


    public FluidStackView(FluidStack value) {
        super(value);

        this.type = new CachedCatalogType<>(value.getFluid());
        this.volume = value.getVolume();
    }

    @JsonDetails
    public Map<String, Object> getData() {
        HashMap<String, Object> data = new HashMap<>();
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
}
