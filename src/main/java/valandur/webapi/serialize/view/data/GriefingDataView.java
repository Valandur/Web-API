package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.GriefingData;
import valandur.webapi.serialize.BaseView;

public class GriefingDataView extends BaseView<GriefingData> {

    @JsonValue
    public boolean grief;


    public GriefingDataView(GriefingData value) {
        super(value);

        this.grief = value.canGrief().get();
    }
}
