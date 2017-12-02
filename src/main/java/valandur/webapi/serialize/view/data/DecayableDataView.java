package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.DecayableData;
import valandur.webapi.api.serialize.BaseView;

public class DecayableDataView extends BaseView<DecayableData> {

    @JsonValue
    public boolean decayable;


    public DecayableDataView(DecayableData value) {
        super(value);

        this.decayable = value.decayable().get();
    }
}
