package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.item.GenerationData;
import valandur.webapi.api.serialize.BaseView;

public class GenerationDataView extends BaseView<GenerationData> {

    @JsonValue
    public int generation;


    public GenerationDataView(GenerationData value) {
        super(value);

        this.generation = value.generation().get();
    }
}
