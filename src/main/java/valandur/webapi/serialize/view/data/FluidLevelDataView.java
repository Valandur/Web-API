package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.FluidLevelData;
import valandur.webapi.serialize.BaseView;

public class FluidLevelDataView extends BaseView<FluidLevelData> {

    @JsonValue
    public int level;


    public FluidLevelDataView(FluidLevelData value) {
        super(value);

        this.level = value.level().get();
    }
}
