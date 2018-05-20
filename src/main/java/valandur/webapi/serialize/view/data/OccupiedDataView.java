package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.OccupiedData;
import valandur.webapi.serialize.BaseView;

public class OccupiedDataView extends BaseView<OccupiedData> {

    @JsonValue
    public boolean occupied;


    public OccupiedDataView(OccupiedData value) {
        super(value);

        this.occupied = value.occupied().get();
    }
}
