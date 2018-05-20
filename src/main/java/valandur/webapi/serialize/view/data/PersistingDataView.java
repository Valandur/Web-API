package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.PersistingData;
import valandur.webapi.serialize.BaseView;

public class PersistingDataView extends BaseView<PersistingData> {

    @JsonValue
    public boolean persists;


    public PersistingDataView(PersistingData value) {
        super(value);

        this.persists = value.persists().get();
    }
}
