package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.SlimeData;
import valandur.webapi.api.serialize.BaseView;

public class SlimeDataView extends BaseView<SlimeData> {

    public int size;


    public SlimeDataView(SlimeData value) {
        super(value);

        this.size = value.size().get();
    }
}
