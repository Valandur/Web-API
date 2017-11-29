package valandur.webapi.serialize.view.data;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.data.manipulator.mutable.tileentity.StructureData;
import org.spongepowered.api.data.type.StructureMode;
import valandur.webapi.api.serialize.BaseView;

public class StructureDataView extends BaseView<StructureData> {

    public String author;
    public boolean ignoreEntities;
    public float integrity;
    public StructureMode mode;
    public Vector3i position;
    public boolean powered;
    public long seed;
    public boolean showAir;
    public boolean showBoundingBox;
    public Vector3i size;


    public StructureDataView(StructureData value) {
        super(value);

        this.author = value.author().get();
        this.ignoreEntities = value.ignoreEntities().get();
        this.integrity = value.integrity().get();
        this.mode = value.mode().get();
        this.position = value.position().get();
        this.powered = value.powered().get();
        this.seed = value.seed().get();
        this.showAir = value.showAir().get();
        this.showBoundingBox = value.showBoundingBox().get();
        this.size = value.size().get();
    }
}
