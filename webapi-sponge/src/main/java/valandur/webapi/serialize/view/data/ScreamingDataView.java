package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.ScreamingData;
import valandur.webapi.serialize.BaseView;

public class ScreamingDataView extends BaseView<ScreamingData> {

    @JsonValue
    public boolean screaming;


    public ScreamingDataView(ScreamingData value) {
        super(value);

        this.screaming = value.screaming().get();
    }
}
