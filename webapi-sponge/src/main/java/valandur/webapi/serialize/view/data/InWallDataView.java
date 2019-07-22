package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.InWallData;
import valandur.webapi.serialize.BaseView;

public class InWallDataView extends BaseView<InWallData> {

    @JsonValue
    public boolean inWall;


    public InWallDataView(InWallData value) {
        super(value);

        this.inWall = value.inWall().get();
    }
}
