package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.block.AttachedData;
import valandur.webapi.api.serialize.BaseView;

public class AttachedDataView extends BaseView<AttachedData> {

    @JsonValue
    public boolean attached;


    public AttachedDataView(AttachedData value) {
        super(value);

        this.attached = value.attached().get();
    }
}
