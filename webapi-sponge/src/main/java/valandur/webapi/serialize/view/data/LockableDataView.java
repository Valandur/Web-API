package valandur.webapi.serialize.view.data;

import com.fasterxml.jackson.annotation.JsonValue;
import org.spongepowered.api.data.manipulator.mutable.tileentity.LockableData;
import valandur.webapi.serialize.BaseView;

public class LockableDataView extends BaseView<LockableData> {

    @JsonValue
    public String lock;


    public LockableDataView(LockableData value) {
        super(value);

        this.lock = value.lockToken().get();
    }
}
