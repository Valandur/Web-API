package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.DropData;
import valandur.webapi.api.serialize.BaseView;

public class DropDataView extends BaseView<DropData> {

    @JsonValue
    public boolean drops;


    public DropDataView(DropData value) {
        super(value);

        this.drops = value.willDrop().get();
    }
}
