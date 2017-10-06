package valandur.webapi.json.view.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ShearedData;
import valandur.webapi.api.json.BaseView;

public class ShearedDataView extends BaseView<ShearedData> {

    @JsonValue
    public boolean sheared;


    public ShearedDataView(ShearedData value) {
        super(value);

        this.sheared = value.sheared().get();
    }
}
