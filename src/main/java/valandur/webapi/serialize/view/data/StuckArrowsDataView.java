package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.StuckArrowsData;
import valandur.webapi.serialize.BaseView;

public class StuckArrowsDataView extends BaseView<StuckArrowsData> {

    @JsonValue
    public int stuckArrows;


    public StuckArrowsDataView(StuckArrowsData value) {
        super(value);

        this.stuckArrows = value.stuckArrows().get();
    }
}
