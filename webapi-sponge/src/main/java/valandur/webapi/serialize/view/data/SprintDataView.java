package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.SprintData;
import valandur.webapi.serialize.BaseView;

public class SprintDataView extends BaseView<SprintData> {

    @JsonValue
    public boolean spriting;


    public SprintDataView(SprintData value) {
        super(value);

        this.spriting = value.sprinting().get();
    }
}
