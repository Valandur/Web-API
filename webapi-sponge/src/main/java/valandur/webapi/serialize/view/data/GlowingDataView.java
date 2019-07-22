package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.GlowingData;
import valandur.webapi.serialize.BaseView;

public class GlowingDataView extends BaseView<GlowingData> {

    @JsonValue
    public boolean glowing;


    public GlowingDataView(GlowingData value) {
        super(value);

        this.glowing = value.glowing().get();
    }
}
