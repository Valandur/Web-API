package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.PigSaddleData;
import valandur.webapi.serialize.BaseView;

public class PigSaddleDataView extends BaseView<PigSaddleData> {

    @JsonValue
    public boolean saddle;


    public PigSaddleDataView(PigSaddleData value) {
        super(value);

        this.saddle = value.saddle().get();
    }
}
