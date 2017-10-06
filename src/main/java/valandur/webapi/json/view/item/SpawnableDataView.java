package valandur.webapi.json.view.item;

import org.spongepowered.api.data.manipulator.mutable.item.SpawnableData;
import valandur.webapi.api.json.BaseView;

public class SpawnableDataView extends BaseView<SpawnableData> {

    public String id;
    public String name;


    public SpawnableDataView(SpawnableData value) {
        super(value);

        this.id = value.type().get().getId();
        this.name = value.type().get().getTranslation().get();
    }
}
