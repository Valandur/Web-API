package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.entity.BreedableData;
import valandur.webapi.serialize.BaseView;

public class BreedableDataView extends BaseView<BreedableData> {

    @JsonValue
    public boolean breedable;


    public BreedableDataView(BreedableData value) {
        super(value);

        this.breedable = value.breedable().get();
    }
}
