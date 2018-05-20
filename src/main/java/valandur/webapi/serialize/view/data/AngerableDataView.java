package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.AngerableData;
import valandur.webapi.serialize.BaseView;

public class AngerableDataView extends BaseView<AngerableData> {

    @JsonValue
    public int anger;


    public AngerableDataView(AngerableData value) {
        super(value);

        this.anger = value.angerLevel().get();
    }
}
