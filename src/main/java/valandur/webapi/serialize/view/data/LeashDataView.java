package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.LeashData;
import org.spongepowered.api.entity.Entity;
import valandur.webapi.api.serialize.BaseView;

public class LeashDataView extends BaseView<LeashData> {

    public Entity holder;


    public LeashDataView(LeashData value) {
        super(value);

        this.holder = value.leashHolder().get();
    }
}
