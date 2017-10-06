package valandur.webapi.json.view.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.TameableData;
import valandur.webapi.api.json.BaseView;

import java.util.UUID;

public class TameableDataView extends BaseView<TameableData> {

    public boolean tamed;
    public UUID owner;


    public TameableDataView(TameableData value) {
        super(value);

        this.tamed = value.owner().exists();
        this.owner = value.owner().get().orElse(null);
    }
}
