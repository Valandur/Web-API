package valandur.webapi.serialize.view.data;

import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import valandur.webapi.api.serialize.BaseView;

public class InvisibilityDataView extends BaseView<InvisibilityData> {

    public boolean ignoreCollision;
    public boolean invisible;
    public boolean untargetable;
    public boolean vanish;


    public InvisibilityDataView(InvisibilityData value) {
        super(value);

        this.ignoreCollision = value.ignoresCollisionDetection().get();
        this.invisible = value.invisible().get();
        this.untargetable = value.untargetable().get();
        this.vanish = value.vanish().get();
    }
}
